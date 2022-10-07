#!/bin/sh

sqlite3 ./options.db << EOF
  CREATE TABLE options(
    discord_id character(18) not null,
    user text not null,
    discriminator character(4) not null,
    is_ignored boolean not null default false,
    chance_weight double not null default 0.5,
    timeout int not null default 0
  );
EOF