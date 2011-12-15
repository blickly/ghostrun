
LOGNAME=dec15logs/serverlog$1

./download_logs.exp
cp outfile.log $LOGNAME
git add dec15logs/ && git commit -m "Raw logs $1"
vim $LOGNAME
git add dec15logs/ && git commit -m "Trimmed logs $1"

