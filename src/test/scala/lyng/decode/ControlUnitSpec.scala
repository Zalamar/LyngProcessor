package lyng.decode

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest._

// test class
class ControlUnitTester(dut: ControlUnit) extends PeekPokeTester(dut) {
    
}

// test spec
class ControlUnitSpec extends FlatSpec with Matchers {
    "Tester" should "pass" in {
        chisel3.iotesters.Driver(() => new ControlUnit) { c =>
            new ControlUnitTester(c)
        } should be (true)
    }
}
