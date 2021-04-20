package lyng.decode

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest._
import lyng.ControlUnitSig
import lyng.RefernceVals
import lyng.decode.ControlUnit

// test class
class ControlUnitTester(dut : ControlUnit) extends PeekPokeTester(dut) {

    def printControlSigCSV(sig : ControlUnitSig) {
        print(peek(sig.reg_write).toString + ",")
        print(peek(sig.mem_write).toString + ",")
        print(peek(sig.mem_read).toString + ",")
        print(((peek(sig.alu_op) >> 3) & 1).toString + ",")
        print(((peek(sig.alu_op) >> 2) & 1).toString + ",")
        print(((peek(sig.alu_op) >> 1) & 1).toString + ",")
        print((peek(sig.alu_op) & 1).toString + ",")
        print(peek(sig.alu_src).toString + ",")
        print(((peek(sig.ext_mode) >> 2) & 1).toString + ",")
        print(((peek(sig.ext_mode) >> 1) & 1).toString + ",")
        print((peek(sig.ext_mode) & 1).toString + ",")
        print(peek(sig.carry_write).toString + ",")
        print(peek(sig.carry_src).toString + ",")
        print(peek(sig.rd_src).toString + ",")
        print(peek(sig.mem_addr_src).toString + ",")
        print(((peek(sig.stack_op) >> 1) & 1).toString + ",")
        print((peek(sig.stack_op) & 1).toString + ",")
        print(peek(sig.jmp_amt_src).toString + ",")
        print(((peek(sig.jmp_mode) >> 2) & 1).toString + ",")
        print(((peek(sig.jmp_mode) >> 1) & 1).toString + ",")
        print((peek(sig.jmp_mode) & 1).toString + ",")
        print(peek(sig.mem_data_src).toString + ",")
        print(((peek(sig.pc_src) >> 1) & 1).toString + ",")
        print((peek(sig.pc_src) & 1).toString + ",")
        print("\n")
    }

    for (s <- RefernceVals.op_ref.keys) {
        print(s + ",")
        poke(dut.io.opcode, RefernceVals.op_ref(s))
        poke(dut.io.func, RefernceVals.func_ref(s))
        printControlSigCSV(dut.io.control)
    }


}

// test spec
class ControlUnitSpec extends FlatSpec with Matchers {
    "Tester" should "pass" in {
        chisel3.iotesters.Driver(() => new ControlUnit) { c =>
            new ControlUnitTester(c)
        } should be (true)
    }
}
