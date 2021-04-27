package lyng

import chisel3._
import chisel3.util._
import scala.collection.immutable.ListMap

object RefernceVals {

  // refernce list of opcodes
  val op_ref = ListMap( "ADD" -> "b00001".U,
                        "ADC" -> "b00001".U,
                        "SUB" -> "b00001".U,
                        "SBB" -> "b00001".U,
                        "AND" -> "b00010".U,
                        "OR" -> "b00010".U,
                        "XOR" -> "b00010".U,
                        "NOT" -> "b00010".U,
                        "SHFL" -> "b00011".U,
                        "SHFA" -> "b00100".U,
                        "ADDI" -> "b00101".U,
                        "SUBI" -> "b00110".U,
                        "MVIH" -> "b00111".U,
                        "MVIL" -> "b01000".U,
                        "LDIDR" -> "b01001".U,
                        "STIDR" -> "b01010".U,
                        "LDIDX" -> "b01011".U,
                        "STIDX" -> "b01100".U,
                        "JMP" -> "b01101".U,
                        "JMPI" -> "b01110".U,
                        "JGEO" -> "b01111".U,
                        "JLEO" -> "b10000".U,
                        "JCO" -> "b10001".U,
                        "JEO" -> "b10010".U,
                        "PUSH" -> "b10011".U,
                        "POP" -> "b10100".U,
                        "CALL" -> "b10101".U,
                        "JAL" -> "b10110".U,
                        "MOVSP" -> "b10111".U,
                        "RET" -> "b11000".U,
                        "STC" -> "b11001".U,
                        "NOP" -> "b00000".U}
  )

  // reference list of func codes
  val func_ref = ListMap("ADD"   -> "b00".U,
                         "ADC"   -> "b01".U,
                         "SUB"   -> "b10".U,
                         "SBB"   -> "b11".U,
                         "AND"   -> "b00".U,
                         "OR"    -> "b01".U,
                         "XOR"   -> "b10".U,
                         "NOT"   -> "b11".U,
                         "SHFL"  -> "b00".U,
                         "SHFA"  -> "b00".U,
                         "ADDI"  -> "b00".U,
                         "SUBI"  -> "b00".U,
                         "MVIH"  -> "b00".U,
                         "MVIL"  -> "b00".U,
                         "LDIDR" -> "b00".U,
                         "STIDR" -> "b00".U,
                         "LDIDX" -> "b00".U,
                         "STIDX" -> "b00".U,
                         "JMP"   -> "b00".U,
                         "JMPI"  -> "b00".U,
                         "JGEO"  -> "b00".U,
                         "JLEO"  -> "b00".U,
                         "JCO"   -> "b00".U,
                         "JEO"   -> "b00".U,
                         "PUSH"  -> "b00".U,
                         "POP"   -> "b00".U,
                         "CALL"  -> "b00".U,
                         "JAL"   -> "b00".U,
                         "MOVSP" -> "b00".U,
                         "RET"   -> "b00".U,
                         "STC"   -> "b00".U
  )

}
