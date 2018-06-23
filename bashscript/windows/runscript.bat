echo This script is about to run another script &

sh ./backendscript.bat &
echo Backend Run

sh ./frontendscript.bat
echo Frontend Run