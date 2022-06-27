package com.balashenka.comrade.util;

import com.balashenka.comrade.util.locale.ReplyLocaleText;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public record AttachmentUtil(MessageUtil messageUtil) {
    @NonNull
    public String buildFinish(@NonNull Map<String, Long> choices) {
        var result = new StringBuilder();

        var winner = new Winner("", 0L);
        result.append(messageUtil.getText(ReplyLocaleText.REPLY_POLL_FINISH_RESULT)).append(MessageUtil.NEW_LINE_HTML_SYMBOL);
        for (var choice : choices.entrySet()) {
            if (choice.getValue() > winner.getScore()) {
                winner.setScore(choice.getValue());
                winner.setText(choice.getKey());
            }

            result.append(messageUtil.getText(ReplyLocaleText.REPLY_POLL_RESULT_ROW, choice.getKey(), choice.getValue().toString())).append(MessageUtil.NEW_LINE_HTML_SYMBOL);
        }

        List<Winner> winners = new ArrayList<>();
        for (var choice : choices.entrySet()) {
            if (choice.getValue().equals(winner.getScore())) {
                winners.add(new Winner(choice.getKey(), choice.getValue()));
            }
        }

        var winnerText = new StringBuilder();
        if (winners.size() > 0 && winner.getScore() > 0) {
            for (var win : winners) {
                winnerText.append(messageUtil.getText(ReplyLocaleText.REPLY_POLL_RESULT_WINNER, win.getText(), win.getScore().toString())).append(MessageUtil.NEW_LINE_HTML_SYMBOL);
            }
        } else {
            winnerText.append(messageUtil.getText(ReplyLocaleText.REPLY_POLL_RESULT_NOT_WINNER)).append(MessageUtil.NEW_LINE_HTML_SYMBOL);
        }

        return winnerText + result.toString();
    }

    @Data
    @AllArgsConstructor
    private static class Winner {
        private String text;
        private Long score;
    }
}
