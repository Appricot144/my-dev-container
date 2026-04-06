#!/bin/zsh
set -e
chown -R $CONTAINER_USER:$CONTAINER_USER /deploy
chown -R $CONTAINER_USER:$CONTAINER_USER /home/$CONTAINER_USER/.m2

exec sleep infinity