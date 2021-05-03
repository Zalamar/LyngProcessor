package lyng

import chisel3._
import chisel3.iotesters.PeekPokeTester
import lyng.execute.ALU
import lyng.EXPropagationUnit
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class PropagationUnitTester(dut: EXPropagationUnit) extends PeekPokeTester(dut) {
    poke(dut.io.ex_me.reg_write, 0)
    poke(dut.io.ex_me.rw_addr, 0)
    poke(dut.io.me_wb.reg_write, 0)
    poke(dut.io.me_wb.rw_addr, 0)
    poke(dut.io.ex_me_mem_read, 0)
    poke(dut.io.rx_addr, 0)
    step(1)
    expect(dut.io.prop_rx, 0, "Error in prop_rx test 0:")
    expect(dut.io.conflict_stall, 0)

    poke(dut.io.ex_me.reg_write, 0)
    poke(dut.io.ex_me.rw_addr, 0)
    poke(dut.io.me_wb.reg_write, 0)
    poke(dut.io.me_wb.rw_addr, 2)
    poke(dut.io.ex_me_mem_read, 0)
    poke(dut.io.rx_addr, 0)
    step(1)
    expect(dut.io.prop_rx, 0, "Error in prop_rx test 1:")
    expect(dut.io.conflict_stall, 0)

    poke(dut.io.ex_me.reg_write, 1)
    poke(dut.io.ex_me.rw_addr, 0)
    poke(dut.io.me_wb.reg_write, 0)
    poke(dut.io.me_wb.rw_addr, 0)
    poke(dut.io.ex_me_mem_read, 0)
    poke(dut.io.rx_addr, 0)
    step(1)
    expect(dut.io.prop_rx, 2, "Error in prop_rx test 2:")
    expect(dut.io.conflict_stall, 0)

    poke(dut.io.ex_me.reg_write, 0)
    poke(dut.io.ex_me.rw_addr, 0)
    poke(dut.io.me_wb.reg_write, 0)
    poke(dut.io.me_wb.rw_addr, 0)
    poke(dut.io.ex_me_mem_read, 0)
    poke(dut.io.rx_addr, 0)
    step(1)
    expect(dut.io.prop_rx, 0, "Error in prop_rx test 3:")
    expect(dut.io.conflict_stall, 0)

    poke(dut.io.ex_me.reg_write, 0)
    poke(dut.io.ex_me.rw_addr, 0)
    poke(dut.io.me_wb.reg_write, 0)
    poke(dut.io.me_wb.rw_addr, 0)
    poke(dut.io.ex_me_mem_read, 0)
    poke(dut.io.rx_addr, 0)
    step(1)
    expect(dut.io.prop_rx, 0, "Error in prop_rx test 4:")
    expect(dut.io.conflict_stall, 0)

    poke(dut.io.ex_me.reg_write, 0)
    poke(dut.io.ex_me.rw_addr, 0)
    poke(dut.io.me_wb.reg_write, 0)
    poke(dut.io.me_wb.rw_addr, 3)
    poke(dut.io.ex_me_mem_read, 1)
    poke(dut.io.rx_addr, 3)
    step(1)
    expect(dut.io.prop_rx, 0, "Error in prop_rx test 4:")
    expect(dut.io.conflict_stall, 1, "Error in conflict_stall test 4:")
}

// test spec
class PropagationUnitSpec extends FlatSpec with Matchers {
  "Tester" should "pass" in {
    chisel3.iotesters.Driver (() => new EXPropagationUnit()) { c =>
      new PropagationUnitTester(c)
    } should be (true)
  }
}