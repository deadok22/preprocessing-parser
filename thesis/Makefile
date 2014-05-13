preview: build
	evince thesis.pdf

build:
	pdflatex thesis.tex
	bibtex thesis.aux
	pdflatex thesis.tex
	pdflatex thesis.tex

clean:
	rm -f *.log *.aux *.pdf *.toc *.bbl *.out *-blx.bib *.blg *.run.xml
