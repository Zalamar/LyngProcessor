SBT = sbt
PY = python3
PROGRAM = program.lisa
.PHONY = vcd clean_vcd install_assembler run_assembler generate test simulate vcd src/test/resources/program.bin



clean:
	rm -f src/test/resources/program.bin
	$(SBT) clean
	echo "---CLEANED ALL TEMP FILES (excluding .vcd traces)---"

clean_vcd:
	rm -rf test_run_dir
	echo "---CLEANED .vcd traces---"

install_assembler:
	git submodule init
	git submodule update
	echo "---ASSEMBLER INSTALLED---"


run_assembler: src/test/resources/program.bin
	echo "---BINARY FILE CREATED---"

src/test/resources/program.bin:
	mkdir -p src/test/resources
	$(PY) lyng-assembler/lyng-assembler.py $(PROGRAM) src/test/resources/program.bin

# Generate Verilog code
generate:
	$(SBT) run
	echo "---VERILOG FILES CREATED IN generated/---"

test:
	$(SBT) test


simulate: run_assembler
	$(PY) lyng-assembler/lyng-assembler.py $(PROGRAM) src/test/resources/program.bin
	$(SBT) "testOnly lyng.Simulator"

vcd: simulate
	sh getVcd.sh
	echo "---.vcd TRACES GENERATED are available in latest.vcd---"