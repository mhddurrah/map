echo "This script is about to run another script."
sh ./backendscript.sh
echo "Backend Run"

sh ./frontendscript.sh
echo "Frontend Run"