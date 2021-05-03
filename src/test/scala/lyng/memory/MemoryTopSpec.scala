package lyng.memory

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FlatSpec, Matchers}
import chisel3.iotesters.Driver


class MemoryTopTester(dut: MemoryTop) extends PeekPokeTester(dut) {
    //No memory operation (ADD)
    poke(dut.io.in.jump, 0)
    poke(dut.io.in.jump_amt, 0)
    poke(dut.io.in.alu_res, 0)
    poke(dut.io.in.pc, 0)
    poke(dut.io.in.rd, 0)
    poke(dut.io.in.rw_addr, 0)
    poke(dut.io.in.prop_ME_ME, 0)
    poke(dut.io.in.rw_value, 0)
    poke(dut.io.in.data_out, 0)


    poke(dut.io.in.jump, 0)
    poke(dut.io.in.jump_amt, 0)
    poke(dut.io.in.alu_res, 0xFFEE)
    poke(dut.io.ctrl.mem_addr_src, 0)
    poke(dut.io.in.rd, 0xFF00)
    expect(dut.io.out.addr, 0xFFEE)
    expect(dut.io.out.data_in,0xFF00)
    step(1)
    println("Testing MOVSP")
    poke(dut.io.ctrl.stack_op, 1)
    poke(dut.io.ctrl.mem_addr_src, 1)
    step(1)
    println("Testing POP and PUSH")
    expect(dut.io.out.addr, 0xFF00)
    poke(dut.io.ctrl.mem_addr_src, 1)
    poke(dut.io.ctrl.stack_op, 2)
    expect(dut.io.out.addr, 0xFF00)
    step(1)
    expect(dut.io.out.addr, 0xFF00 - 2)
    poke(dut.io.ctrl.stack_op, 3)
    expect(dut.io.out.addr, 0xFF00)

}


// test spec
class MemoryTopSpec extends FlatSpec with Matchers {
  "Tester" should "pass" in {
      Driver.execute(Array("--generate-vcd-output", "on"), () => new MemoryTop()) {
          c => new MemoryTopTester(c)
      } should be (true)
  }
}