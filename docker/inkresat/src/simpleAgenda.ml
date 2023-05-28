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

open Syntax

type t = { high : (Minisat.lit * int) list ref; low : (Minisat.lit * int) list ref }

let create () = { high = ref []; low = ref [] }

let is_empty a = match !(a.high), !(a.low) with [], [] -> true | _, _ -> false

let add a p id =
  match DynArray.get id_to_fml id with
      Box _ -> a.low := (p, id) :: !(a.low)
    | _ -> a.high := (p, id) :: !(a.high)

let pop a = match !(a.high) with
    [] -> (match !(a.low) with [] -> None | h::t -> a.low := t; Some h)
  | h::t -> a.high := t; Some h

let clear a = a.high := []; a.low := []
