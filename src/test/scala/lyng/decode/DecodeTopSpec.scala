package lyng.decode

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest._
import lyng.RefernceVals

// test class
class DecodeTopTester(dut : DecodeTop) extends PeekPokeTester(dut) {

    def storeToReg(regAddr : Int, value : Int) = {
        poke(dut.io.in.wb_rw_value, value)
        poke(dut.io.in.rw_addr, regAddr)
        poke(dut.io.in.wb_reg_write, 1)
        step(1)
    }

    def getADDInstr(rd : Int, rs1 : Int, rs2 : Int) : Int = {
        var instr = rd << 5
        instr += rs1 << 8
        instr += rs2 << 2
        return instr
    }

    // load reg file
    for (i <- 0 until 8) {
        storeToReg(i, i*i)
    }
    // set instruction
    poke(dut.io.in.instr, getADDInstr(3, 4, 5))
    expect(dut.io.out.rd, 9)
    expect(dut.io.out.rs1, 16)
    expect(dut.io.out.rs2, 25)
}

// test spec
class DecodeTopSpec extends FlatSpec with Matchers {
    "Tester" should "pass" in {
        chisel3.iotesters.Driver(() => new DecodeTop) { c =>
            new DecodeTopTester(c)
        } should be (true)
    }
}
