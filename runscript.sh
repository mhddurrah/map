echo "This script is about to run another script."
lsof -ti:8080 | xargs kill & lsof -ti:4200 | xargs kill
sh ./backendscript.sh &
echo "Backend Run"

sh ./frontendscript.sh
echo "Frontend Run"