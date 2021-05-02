SBT = sbt
PY = python3
PROGRAM = program.lisa




clean:
	rm -f src/test/resources/program.bin
	$(SBT) clean
	git clean -fd

install_assembler:
	git submodule init
	git submodule update

run_assembler:
	$(PY) lyng-assembler/lyng-assembler.py $(PROGRAM) src/test/resources/program.bin

# Generate Verilog code
generate:
	$(SBT) run


simulate:
	$(PY) lyng-assembler/lyng-assembler.py $(PROGRAM) src/test/resources/program.bin
	$(SBT) "testOnly lyng.Simulator"

vcd:
	sh getVcd.sh