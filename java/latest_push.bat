@echo off
curl --silent https://api.github.com/users/rlwhitcomb/events/public | call c latest_push.calc -nolib -nocolor -clear -- @
