CREATE TABLE groups
(
    id      UUID NOT NULL,
    name    TEXT NOT NULL,
    team_id TEXT NOT NULL,
    CONSTRAINT pk_groups PRIMARY KEY (id)
);

ALTER TABLE groups
    ADD CONSTRAINT uc_groups_name UNIQUE (name);

ALTER TABLE groups
    ADD CONSTRAINT uc_groups_team_id UNIQUE (team_id);

CREATE TABLE persons
(
    id                 UUID    NOT NULL,
    day                INTEGER NOT NULL,
    month              INTEGER NOT NULL,
    name               TEXT    NOT NULL,
    email              TEXT    NOT NULL,
    is_moderator       BOOLEAN NOT NULL,
    team_membership_id TEXT    NOT NULL,
    wishlist           TEXT,
    is_notified        BOOLEAN NOT NULL,
    is_ignoring        BOOLEAN NOT NULL,
    group_id           UUID    NOT NULL,
    CONSTRAINT pk_persons PRIMARY KEY (id)
);

ALTER TABLE persons
    ADD CONSTRAINT uc_persons_team_membership_id UNIQUE (team_membership_id);

ALTER TABLE persons
    ADD CONSTRAINT fk_persons_on_group FOREIGN KEY (group_id) REFERENCES groups (id);

CREATE TABLE spaces
(
    id           UUID                     NOT NULL,
    created_date TIMESTAMP with time zone NOT NULL,
    title        TEXT                     NOT NULL,
    room_id      TEXT                     NOT NULL,
    message_id   TEXT,
    person_id    UUID                     NOT NULL,
    CONSTRAINT pk_spaces PRIMARY KEY (id)
);

ALTER TABLE spaces
    ADD CONSTRAINT uc_spaces_room_id UNIQUE (room_id);

ALTER TABLE spaces
    ADD CONSTRAINT uc_spaces_message_id UNIQUE (message_id);

ALTER TABLE spaces
    ADD CONSTRAINT fk_spaces_on_person FOREIGN KEY (person_id) REFERENCES persons (id);

CREATE TABLE polls
(
    id           UUID                     NOT NULL,
    title        TEXT                     NOT NULL,
    room_id      TEXT                     NOT NULL,
    created_date TIMESTAMP with time zone NOT NULL,
    message_id   TEXT                     NOT NULL,
    CONSTRAINT pk_polls PRIMARY KEY (id)
);

ALTER TABLE polls
    ADD CONSTRAINT uc_polls_message_id UNIQUE (message_id);

CREATE TABLE choices
(
    id          UUID   NOT NULL,
    amount      BIGINT NOT NULL,
    choice_text TEXT   NOT NULL,
    poll_id     UUID   NOT NULL,
    CONSTRAINT pk_choices PRIMARY KEY (id)
);

ALTER TABLE choices
    ADD CONSTRAINT fk_choices_on_poll FOREIGN KEY (poll_id) REFERENCES polls (id);
