package lyng

import chisel3._
import chisel3.util._
import scala.collection.immutable.ListMap

object RefernceVals {

  // refernce list of opcodes
  val op_ref = ListMap("ADD" -> "b00000".U,
                       "ADC" -> "b00000".U,
                       "SUB" -> "b00000".U,
                       "SBB" -> "b00000".U,
                       "AND" -> "b00001".U,
                       "OR" -> "b00001".U,
                       "XOR" -> "b00001".U,
                       "NOT" -> "b00001".U,
                       "SHFL" -> "b00010".U,
                       "SHFA" -> "b00011".U,
                       "ADDI" -> "b00100".U,
                       "SUBI" -> "b00101".U,
                       "MVIH" -> "b00110".U,
                       "MVIL" -> "b00111".U,
                       "LDIDR" -> "b01000".U,
                       "STIDR" -> "b01001".U,
                       "LDIDX" -> "b01010".U,
                       "STIDX" -> "b01011".U,
                       "JMP" -> "b01100".U,
                       "JMPI" -> "b01101".U,
                       "JGEO" -> "b01110".U,
                       "JLEO" -> "b01111".U,
                       "JCO" -> "b10000".U,
                       "JEO" -> "b10001".U,
                       "PUSH" -> "b10010".U,
                       "POP" -> "b10011".U,
                       "CALL" -> "b10100".U,
                       "JAL" -> "b10101".U,
                       "MOVSP" -> "b10110".U,
                       "RET" -> "b10111".U,
                       "STC" -> "b11000".U
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
