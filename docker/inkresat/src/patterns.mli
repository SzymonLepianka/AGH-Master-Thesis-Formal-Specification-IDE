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

type prefix = Minisat.lit
type pattern

val patterns_new      : int ref
val patterns_blocked  : int ref
val dummy_pattern     : pattern

val init              : int -> (prefix, Minisat.lit * int) Hashtbl.t array -> unit
val add_diamond       : prefix -> Minisat.lit -> Syntax.rel -> int -> unit
val add_box           : prefix -> Syntax.rel -> int -> unit
val get_pattern       : prefix -> Syntax.rel -> int -> pattern
val expand            : pattern -> unit
val is_expanded       : pattern -> bool
val unblock_subtree   : Minisat.lit -> unit
val update_from_model : unit -> unit
(*val extend_patterns   : prefix -> Syntax.rel -> unit*)
