package ru.javaops.masterjava.service.model;

import lombok.*;
import ru.javaops.masterjava.persist.model.BaseEntity;

import java.time.LocalDateTime;

/**
 * @author A.Khalitova
 * 24-Jan-2022
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MailState extends BaseEntity {

    private @NonNull String list_to;
    private @NonNull String list_cc;
    private @NonNull String subject;
    private @NonNull String state;
    private @NonNull LocalDateTime datetime;
}
