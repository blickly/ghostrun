6.out: *.6
	6l $^

%.6: %.go
	6g $^

clean:
	$(RM) *.6 *~ *.swp *.out

run: 6.out
	time ./6.out
