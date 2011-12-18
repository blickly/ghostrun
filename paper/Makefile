latex = (pdflatex -interaction=nonstopmode $(1) \
	| grep -A3 "LaTeX Warning\|LaTeX Error\|\!" \
	|| true)


PAPER=PacmanPaper

all: ${PAPER}.pdf

${PAPER}.pdf: ${PAPER}.tex refs.bib *.cls figs/*.pdf
	$(call latex,${PAPER}.tex) &> /dev/null
	bibtex ${PAPER}
	$(call latex,${PAPER}.tex) &> /dev/null
	$(call latex,${PAPER}.tex)

clean:
	$(RM) -Rf *.aux *.bbl *.blg *.log *~
distclean: clean
	$(RM) -Rf *.pdf
