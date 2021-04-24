python3 ../lyng-assembler/lyng-assembler.py program.lisa src/test/resources/program.bin
sbt "testOnly lyng.Simulator"
LATEST=$(ls -td test_run_dir/*/ | head -1)
VCD=$(ls $LATEST*.vcd | head -1)
cp $VCD latest.vcd
