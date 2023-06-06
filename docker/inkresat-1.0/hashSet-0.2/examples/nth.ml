(* example adapted from the nth-nearest Neighbours example in
 http://www.ffconsultancy.com/products/ocaml_for_scientists/complete/
 to run this example one must use the lexer, parser and data files
 in the nth directory which can be downloaded from this URL.
 Compile with
 ocamlopt -noassert -unsafe -inline 10 hashSet.cmx unix.cmxa lexer.cmx
 parser.cmx nth.ml -o nth
 To check that the results are the same as with Harrop's nth.ml program
 using Set, one can order the list nn according to Hashtbl.hash
 *)
let singleton x =
  let s = HashSet.empty () in
  HashSet.add s x;
  s

let list_init n f =
  let rec aux l = function
      0 -> l
    | n -> aux (f n :: l) (n-1) in
  aux [] n

let add_i = List.map2 ( + )

let list_rev_iteri f l =
  ignore (List.fold_left (fun n e -> f n e; n+1) 0 l)

let list_of_set s = HashSet.fold (fun e l -> e::l) s []

let set_of_list l =
    let r = ref (HashSet.empty ()) in
    List.iter (fun s -> HashSet.add !r s) l;
    !r;;

let rec list_map3 f l1 l2 l3 = match l1, l2, l3 with
  h1::t1, h2::t2, h3::t3 ->
    f h1 h2 h3 :: list_map3 f t1 t2 t3
| [], [], [] -> []
| _ -> invalid_arg "list_map3"

let _ =
  let n, i = match Sys.argv with
      [|_; n; i|] -> int_of_string n, int_of_string i
    | _ -> invalid_arg "Usage: nth <n> <i>" in

  let supercell, pos, bonds =
    let supercell, atoms =
      try
	let lexbuf = Lexing.from_channel stdin in
	Parser.main Lexer.token lexbuf
      with Parsing.Parse_error ->
	let line = string_of_int !Lexer.line in
	print_endline ("Parse error at line "^line);
	exit 1 in
    let bonds = Array.create (List.length atoms) (HashSet.empty ())
    and pos = Array.create (List.length atoms) [] in
    let aux i (r, l) =
      pos.(i) <- r;
      bonds.(i) <- set_of_list l in
    list_rev_iteri aux atoms;
    supercell, pos, bonds in

  let _ =
    let d = List.length supercell in
    let test l = assert (d = List.length l) in
    Array.fold_left (fun () -> test) () pos;
    let aux () l =
      HashSet.fold (fun (_, l) () -> test l) l () in
    Array.fold_left aux () bonds;
    assert (0 < i && i <= Array.length pos) in

  let zero =
    list_init (List.length supercell) (fun i -> 0) in
  
  let rec nth_nn =
    let memory = Hashtbl.create 1 in
    fun n (i, io) ->
      try Hashtbl.find memory (n, i)
      with Not_found -> match n with
	0 -> singleton (i, io)
      | 1 ->
	  let nn = bonds.(i - 1) in
	  if io = zero then nn else
              let aux (j, jo) s = ((HashSet.add s (j, add_i io jo)); s) in
	  HashSet.fold aux nn (HashSet.empty ())
      | n ->
	  let pprev = nth_nn (n-2) (i, io) in
	  let prev = nth_nn (n-1) (i, io) in
          let aux j t = HashSet.update t (nth_nn 1 j); t in
          let t = HashSet.empty () in
          let _ = HashSet.fold aux prev t in
          HashSet.diff_update (HashSet.diff_update t prev;t) pprev;
	  Hashtbl.add memory (n, i) t;
	  t in

  let pos_of (i, io) =
    let aux io s r = r +. s *. float_of_int io in
    let r = list_map3 aux io supercell pos.(i - 1) in
    let r = List.map string_of_float r in
    "{"^(String.concat ", " r)^"}" in

  let nn = list_of_set (nth_nn n (i, zero)) in
  let nn = List.sort (fun x y -> (Hashtbl.hash x) - (Hashtbl.hash y)) nn in
  let num_nn =  (List.length nn) in
  let nn = String.concat ", " (List.map pos_of nn) in
  print_endline ("{"^nn^"}");
  Printf.printf "\nno. elements = %d\n" num_nn
