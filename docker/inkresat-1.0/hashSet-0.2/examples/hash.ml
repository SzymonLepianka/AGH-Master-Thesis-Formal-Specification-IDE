(* example adapted from
 * http://shootout.alioth.debian.org/old/benchmark.php?test=hash&lang=ocaml&id=0
 *)
let hexdigits =  [| '0'; '1'; '2'; '3'; '4'; '5'; '6'; '7';
                    '8'; '9'; 'a'; 'b'; 'c'; 'd'; 'e'; 'f'; |]

let buf = String.create 32

let rec hexstring_of_int n idx len =
  if n <= 0 then String.sub buf idx len
  else begin
    let new_idx = idx - 1
    and new_len = len + 1 in
    String.set buf new_idx hexdigits.(n land 15);
    hexstring_of_int (n lsr 4) new_idx new_len
  end


let _ =
  let n =
    try int_of_string Sys.argv.(1)
    with Invalid_argument _ -> 1 in
  let hx = HashSet.create n "" in
  let hex_a = Array.make (n+1) "" in
  for i = 1 to n do
    hex_a.(i) <- (hexstring_of_int i 32 0)
  done;

  let int_a = Array.make (n+1) "" in
  for i = 1 to n do
    int_a.(i) <- (string_of_int i)
  done;

  let t0 =  Unix.gettimeofday() in
  for i = 1 to n do
    HashSet.add hx hex_a.(i)
  done;
  let c = ref 0 in
  for i = n downto 1 do
    if HashSet.mem hx int_a.(i) then incr c
  done;
  let t1 =  Unix.gettimeofday() in
  Printf.printf "%d\n" !c;
  Printf.printf "timing %.3f\n" (t1 -. t0)
