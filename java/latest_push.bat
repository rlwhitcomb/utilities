@echo off
curl --silent --output github.json https://api.github.com/users/rlwhitcomb/events/public
call c latest_push.calc -clear -nolib -nocolor -- github.json
del github.json
