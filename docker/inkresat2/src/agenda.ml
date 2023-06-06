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

type t = { queues : SimpleAgenda.t array; queue : int ref }

let create () = { queues = Array.init 5 (fun _ -> SimpleAgenda.create()); queue = ref 0 }

let priority fml = match fml with
  Lit -> 0
| False -> 0
| True -> 0
| Box _ -> 0
| And _ -> 0
| Dia _ -> 0
| Or _ -> 1

let clear a = Array.iter SimpleAgenda.clear a.queues; a.queue := 5

let is_empty a = !(a.queue) >= 5 || SimpleAgenda.is_empty a.queues.(!(a.queue))

let add a p id =
  (*print_endline ("agenda: adding " ^ Minisat.string_of_lit p ^ ":" ^ string_of_id id
		 ^ "  a.queue: " ^ string_of_int !(a.queue));*)
  let q = if !Options.agenda then priority(DynArray.get id_to_fml id) else 0 in
    if !(a.queue) > q then a.queue := q;
    SimpleAgenda.add a.queues.(q) p id

let pop a =
  if !(a.queue) >= 5 then None
  else SimpleAgenda.pop a.queues.(!(a.queue))
