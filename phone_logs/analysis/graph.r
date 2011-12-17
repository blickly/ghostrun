library(Hmisc)

queueData <- read.table("serverQueueLatency.log", header=FALSE, sep=" ", dec=".", col.names=c("Phones","time","pid","minl","meanl","maxl"))

means = aggregate(queueData, by=list(queueData$Phones), FUN=mean)
devs = aggregate(queueData, by=list(queueData$Phones), FUN=sd)

errbar(means$Phones, means$maxl, means$maxl+devs$maxl, means$maxl-devs$maxl, type="b", errbar.col="red", col="red", log = 'x', main="Server Queue Latency", xlab="# Phones", ylab="Wait time (ms)")
errbar(means$Phones, means$meanl, means$meanl+devs$meanl, means$meanl-devs$meanl, type="b", errbar.col="blue", col="blue", add=TRUE)
errbar(means$Phones, means$minl, means$minl+devs$minl, means$minl-devs$minl, type="b", errbar.col="green", col="green", add=TRUE)

dev.copy2pdf(file="serverQueueLatency.pdf")
