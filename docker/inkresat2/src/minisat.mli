(*****************************************************************************
 *  Author:
 *    Mark Kaminski <kaminski@ps.uni-saarland.de>
 *
 *  Copyright:
 *    Mark Kaminski, 2012
 *
 *  This file is part of InKreSAT.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *****************************************************************************)

val init               : int -> unit (* parameter: verbosity level *)
val add_var            : unit -> int
val add_unit_clause    : int -> bool -> bool
val add_binary_clause  : int -> bool -> int -> bool -> bool
val add_ternary_clause : int -> bool -> int -> bool -> int -> bool -> bool
val solve              : unit -> bool
val is_var_true        : int -> bool
val freeze_var         : int -> unit
val thaw_var           : int -> unit

type lit

val to_lit              : int -> bool -> lit
val negate              : lit -> lit
val add_unit_lclause    : lit -> bool
val add_binary_lclause  : lit -> lit -> bool
val add_ternary_lclause : lit -> lit -> lit -> bool
val add_lclause         : lit list -> bool
val is_lit_true         : lit -> bool
val dummy_lit           : lit
val extend              : unit -> bool
val solve_assuming      : lit list -> bool

val string_of_lit       : lit -> string
