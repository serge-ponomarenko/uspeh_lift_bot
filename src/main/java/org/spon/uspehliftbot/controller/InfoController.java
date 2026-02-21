package org.spon.uspehliftbot.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.spon.uspehliftbot.ScheduledTasks;
import org.spon.uspehliftbot.entity.User;
import org.spon.uspehliftbot.entity.UserAction;
import org.spon.uspehliftbot.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class InfoController {

    private final UserRepository userRepository;
    private final ScheduledTasks scheduledTasks;

    @GetMapping(value = "/users", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getUsers() {
        return formatUsersAnswer();
    }

    @GetMapping(value = "/elevators", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getElevators() {
        return formatElevatorsAnswer();
    }

    @GetMapping(value = "/log", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getLog() {
        Path path = Paths.get("./log/spring.log");

        try {
            return "<pre>" + String.join("<br>", Files.readAllLines(path)) + "</pre>";
        } catch (IOException e) {
            return "Could not read log file!\n" + e.getMessage();
        }

    }

    private String formatUsersAnswer() {
        StringBuilder sb = new StringBuilder();
        Map<Integer, Long> userNumsBySection = userRepository.findAll().
                stream()
                .map(User::getUserSection)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        int userCount = userRepository.findAll().size();

        sb.append("<html\n>")
                .append("<head><style>table, th, td {\nborder: 1px solid black;\nborder-collapse: collapse;\npadding: 3px;\n}\n</style>\n</head>\n")
                .append("<body>\n");

        sb.append("<table>\n")
                .append("<tr><th>Section</th><th>Users</th></tr>\n");
        userNumsBySection.forEach((section, count) -> sb
                .append("<tr><td>")
                .append(section)
                .append("</td><td>")
                .append(count)
                .append("</td><tr>\n")
        );
        sb.append("<tr><td>")
                .append("<b>Total users</b>")
                .append("</td><td><b>")
                .append(userCount)
                .append("</b></td><tr>\n");
        sb.append("</table>\n");
        sb.append("<br>\n");

        sb.append("<table>\n")
                .append("<tr>")
                .append("<th>ChatId</th>")
                .append("<th>UserName</th>")
                .append("<th>Name</th>")
                .append("<th>Section</th>")
                .append("<th>Total Enters</th>")
                .append("<th>Total Exits</th>")
                .append("<th>Total Stucks</th>")
                .append("<th>Passenger used</th>")
                .append("<th>Cargo used</th>")
                .append("<th>Join Date</th>")
                .append("</tr>\n");
        userRepository.findAll()
                .stream().sorted(getUserJoinedDateComparator().thenComparingLong(User::getChatId))
                .forEach(user -> sb
                        .append("<tr><td>")
                        .append(user.getChatId())
                        .append("</td><td>")
                        .append(user.getUserName())
                        .append("</td><td>")
                        .append(user.getName())
                        .append("</td><td>")
                        .append(user.getUserSection())
                        .append("</td><td>")
                        .append(user.getUserActions().stream()
                                .filter(action -> action.getAction() == UserAction.Action.ENTER).count())
                        .append("</td><td>")
                        .append(user.getUserActions().stream()
                                .filter(action -> action.getAction() == UserAction.Action.EXIT).count())
                        .append("</td><td>")
                        .append(user.getUserActions().stream()
                                .filter(action -> action.getAction() == UserAction.Action.STUCK).count())
                        .append("</td><td>")
                        .append(user.getUserActions().stream()
                                .filter(action -> action.getAction() == UserAction.Action.ENTER && action.getIsPassengerLift()).count())
                        .append("</td><td>")
                        .append(user.getUserActions().stream()
                                .filter(action -> action.getAction() == UserAction.Action.ENTER && !action.getIsPassengerLift()).count())
                        .append("</td><td>")
                        .append(user.getJoinDate())
                        .append("</td><tr>\n")
                );
        sb.append("</table>\n");

        sb.append("</body></html>\n");
        return sb.toString();
    }

    private @NotNull String formatElevatorsAnswer() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>")
                .append("<head><style>table, th, td {\nborder: 1px solid black;\nborder-collapse: collapse;\npadding: 3px;\n}\n</style>\n</head>")
                .append("<body><table>")
                .append("<tr><th>User</th><th>Section</th><th>isDoneReminder</th><th>isDoneAlarm</th></tr>\n");
        scheduledTasks.getScheduledFutures().forEach((user, st) -> {
                    sb.append("<tr>")
                            .append("<td>").append(user.getUserName()).append("</td>")
                            .append("<td>").append(user.getUserSection()).append("</td>");
                    st.forEach(t ->
                            sb.append("<td>").append(t.isDone()).append("</td>"));
                    sb.append("</tr>\n");

                }
        );
        sb.append("</table></body></html>\n");
        return sb.toString();
    }

    private static @NotNull Comparator<User> getUserJoinedDateComparator() {
        return (o1, o2) -> {
            if (o1.getJoinDate() == null) return -1;
            if (o2.getJoinDate() == null) return 1;
            return o1.getJoinDate().compareTo(o2.getJoinDate());
        };
    }

}
