library(Hmisc)

queueData <- read.table("serverQueueLatency.log", header=FALSE, sep=" ", dec=".", col.names=c("Phones","time","pid","minl","meanl","maxl")) 
means = aggregate(queueData, by=list(queueData$Phones), FUN=mean)
devs = aggregate(queueData, by=list(queueData$Phones), FUN=sd)

errbar(means$Phones, means$maxl, means$maxl+devs$maxl, means$maxl-devs$maxl, type="b", pch=5, errbar.col="red", col="red", log = 'x', main="Server Queue Latency", xlab="# Simulated Phones", ylab="Wait time (ms)")
errbar(means$Phones, means$meanl, means$meanl+devs$meanl, means$meanl-devs$meanl, type="b", pch=2, errbar.col="blue", col="blue", add=TRUE)
errbar(means$Phones, means$minl, means$minl+devs$minl, means$minl-devs$minl, type="b", pch=1, errbar.col="green", col="green", add=TRUE)

legend("topleft", c("Age of newest update","Age of oldest update","Average age of updates"), pch=c(1,5,2), col=c("green","red","blue"))
title(main="Server Queue Latency")

dev.copy2pdf(file="serverQueueLatency.pdf")



phoneData <- read.table("phoneLatency.log", header=FALSE, col.names=c("Phones","realphones","updater","dbm","ecio","d1","d2","d3","sleeptime"))

phoneData = phoneData[phoneData$Phones < phoneData$realphones+3,]

pmeans = aggregate(phoneData, by=list(phoneData$Phones), FUN=mean)
pdevs = aggregate(phoneData, by=list(phoneData$Phones), FUN=sd)

errbar(pmeans$Phones, pmeans$updater, pmeans$updater+pdevs$updater, pmeans$updater-pdevs$updater, type="b", errbar.col="purple4", col="purple4", log = 'x', main="Phone Update Rate", xlab="# Simulated Phones", ylab="Time Between Posts (ms)")
title( main="Update Rate")

dev.copy2pdf(file="phoneUpdateRate.pdf")

errbar(pmeans$Phones, pmeans$d2, pmeans$d2+pdevs$d2, pmeans$d2-pdevs$d2, type="b", pch=1, errbar.col="red", col="red", log = 'x', main="Update Rate Broken Down", xlab="# Simulated Phones", ylab="Time (ms)")
errbar(pmeans$Phones, pmeans$sleeptime, pmeans$sleeptime+pdevs$sleeptime, pmeans$sleeptime-pdevs$sleeptime, type="b", pch=2, errbar.col="blue", col="blue", add=TRUE)
errbar(pmeans$Phones, pmeans$d3, pmeans$d3+pdevs$d3, pmeans$d3-pdevs$d3, type="b", errbar.col="green", col="green", pch=5, add=TRUE)
legend("topleft", c("Server latency","Draw latency","Event wait time"), pch=c(1,5,2), col=c("red","green","blue"))
title( main="Update Rate (detailed)")

dev.copy2pdf(file="phoneUpdateRateDetailed.pdf")


signalStrength <- read.table("signalStrength.log", header=FALSE, col.names=c("realphones","dbm","ecio","d1","d2","d3","sleeptime","updater"))

dmeans = aggregate(signalStrength, by=list(signalStrength$dbm), FUN=mean)
ddevs = aggregate(signalStrength, by=list(signalStrength$dbm), FUN=sd)
emeans = aggregate(signalStrength, by=list(signalStrength$ecio), FUN=mean)
edevs = aggregate(signalStrength, by=list(signalStrength$ecio), FUN=sd)

errbar(dmeans$dbm, dmeans$d2, dmeans$d2+ddevs$d2, dmeans$d2-ddevs$d2, type="b", errbar.col="green", col="green", xlab="RSSI (dBm)", ylab="Server latency (ms)")
title( main="Latency vs. Received signal strength")
dev.copy2pdf(file="latencyVsRSSI.pdf")

emeans$ecio <- 10*emeans$ecio
edevs$ecio <- 10*edevs$ecio
errbar(emeans$ecio, emeans$d2, emeans$d2+edevs$d2, emeans$d2-edevs$d2, type="b", errbar.col="red", col="red", xlab="Ec/Io (dB)", ylab="Server latency (ms)")
title( main="Latency vs. Ec/Io")
dev.copy2pdf(file="latencyVsEcIo.pdf")
