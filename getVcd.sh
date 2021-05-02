LATEST=$(ls -td test_run_dir/*/ | head -1)
VCD=$(ls $LATEST*.vcd | head -1)
cp $VCD latest.vcd
echo "Copied .vcd on latest.vcd"