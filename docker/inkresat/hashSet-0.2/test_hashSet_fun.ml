open OUnit
open Dumper

module IntHashSet = HashSet.Make(struct
  type t = int
  let compare (i:t) j =  (compare i j)
  let hash = Hashtbl.hash
end)


module StringHashSet = HashSet.Make(struct
  type t = string
  let compare (i:t) j =  (compare i j)
  let hash = Hashtbl.hash
end)

let random_state =  Random.get_state();;

let test1 () =
  let h = IntHashSet.create 10 0 in
  IntHashSet.add h 0;
  IntHashSet.add h 1;
  assert_bool "a" (IntHashSet.mem h 1);
  assert_bool "b" (not (IntHashSet.mem h 2) );
  IntHashSet.remove h 0;
  assert_bool "c" (not (IntHashSet.mem h 0) );
  IntHashSet.add h 12;
  IntHashSet.add h 2;
  assert_bool "d" (IntHashSet.mem h 2);      
;;
  
let test2 () =
  let h1 = StringHashSet.create 10 "" in
  StringHashSet.add h1 "A";
  StringHashSet.add h1 "B";
  assert_bool "a" ((StringHashSet.keys h1) = [|"A";"B"|]);
  StringHashSet.add h1 "B";
  assert_bool "b" ((StringHashSet.keys h1) = [|"A";"B"|]);
;;

let test3 () =
  let h = IntHashSet.create 10 0 in
  IntHashSet.add h 0;
  IntHashSet.add h 1;
  IntHashSet.remove h 0;
  assert_bool "a" ((IntHashSet.keys h) = [|1|]);;
 
let test4 () =
  let h = StringHashSet.create 10 "" in
  StringHashSet.add h "A";
  StringHashSet.add h "B";
  StringHashSet.add h "At";
  StringHashSet.add h "Ba";
  (* "At" and "Ba" have the same hash; "Ba" is in the table; see readme *)
  assert_bool "a" (StringHashSet.mem h "Ba");
  assert_bool "b" ((StringHashSet.keys h) =  [|"At"; "A"; "B"; "Ba"|]);
  StringHashSet.add h "Ba";
  assert_bool "c" ((StringHashSet.length h) = 4);
  assert_bool "d" (StringHashSet.mem h "Ba");
  StringHashSet.add h "Bt";
  StringHashSet.add h "Ca";
  assert_bool "e" ((StringHashSet.keys h) = [|"Bt"; "At"; "A"; "B"; "Ca"; "Ba"|]);
  StringHashSet.add h "Da";
  StringHashSet.add h "Ct";
  assert_bool "f" (StringHashSet.mem h "Ct");
      
;;

let test5 () =
  let h = StringHashSet.create 10 ""in
  StringHashSet.add h "A";
  StringHashSet.add h "B";
  StringHashSet.add h "At";
  StringHashSet.add h "Ba";
  (* "At" and "Ba" have the same hash; "Ba" is in the table; see readme *)
  assert_bool "a" ((StringHashSet.length h) = 4);

  StringHashSet.remove h "Ba";
  assert_bool "b" ((StringHashSet.length h) = 3); 

  StringHashSet.add h "Ba";
  assert_bool "c" ((StringHashSet.length h) = 4); 
  StringHashSet.remove h "At";
  assert_bool "d" ((StringHashSet.length h) = 3); 
;;

  
let test6 () =
  let h = StringHashSet.create 4 ""in
  assert_bool "a" ((StringHashSet.length h) = 0);
  StringHashSet.add h "AtAtAtAt";
  StringHashSet.add h "AtAtAtBa";
  StringHashSet.add h "AtAtBaAt";
  StringHashSet.add h "AtAtBaBa";
  StringHashSet.add h "AtBaAtAt";
  StringHashSet.add h "AtBaAtBa";
  StringHashSet.add h "AtBaBaAt";
  StringHashSet.add h "AtBaBaBa";
  StringHashSet.add h "BaAtAtAt";
  StringHashSet.add h "BaAtAtBa";
  StringHashSet.add h "BaAtBaAt";
  assert_bool "b" ((StringHashSet.length h) = 11); 
;; 
 

let test7 () =
  let h = IntHashSet.create 4 0 in
  assert_bool "a" ((IntHashSet.length h) = 0); 
  for i=0 to 10 do
    IntHashSet.add h i;
  done;
  assert_bool "b" ((IntHashSet.length h) = 11); 

  for i=11 to 14 do
    IntHashSet.add h i;
  done;
  assert_bool "d" ((IntHashSet.length h) = 15); 
;;

let test8 () =
  (* it caught a bug in resize *)
  let n = 10 in
  let my_hash = IntHashSet.create n 0 in
  
  for i=1 to n do
      IntHashSet.add my_hash i;
  done;
  assert_bool "a" ((IntHashSet.length my_hash) = 10);
  for i=1 to n do
      IntHashSet.add my_hash i;
  done;
  assert_bool "b" ((IntHashSet.length my_hash) = 10);
;;

let test9 () =
  let h = StringHashSet.create 10 "" in
  StringHashSet.add h "A";
  StringHashSet.add h "B";
  StringHashSet.add h "At";
  StringHashSet.add h "Ba";
  let a = ref [] in
  StringHashSet.iter (fun k -> (a := k :: !a)) h;
  assert_bool "a" (!a =  ["Ba"; "B" ; "A"; "At"]);
  assert_bool "b" ((StringHashSet.length h) = 4); 
  StringHashSet.clear h;
  assert_bool "c" ((StringHashSet.length h) = 0); 
  assert_bool "d" ((StringHashSet.keys h) = [| |]);
;;  

let test10 () =
  let h = StringHashSet.create 10 "" in
  StringHashSet.add h "A";
  StringHashSet.add h "B";
  StringHashSet.add h "At";
  StringHashSet.add h "Ba";
  let h2 = StringHashSet.create 4 "" in
  StringHashSet.add h2 "B";
  StringHashSet.add h2 "Ba";
  StringHashSet.add h2 "C";
  let h3 = StringHashSet.union h h2 in
  assert_bool "a" ((StringHashSet.keys h3) =    [|"At"; "A"; "B"; "C"; "Ba"|]);
  StringHashSet.update h h2;
  assert_bool "b" ((StringHashSet.keys h) =    [|"At"; "A"; "B"; "C"; "Ba"|]);
;;  


let test11 () =
  let h = StringHashSet.create 10 "" in
  StringHashSet.add h "A";
  StringHashSet.add h "B";
  StringHashSet.add h "At";
  StringHashSet.add h "Ba";
  assert_bool "a" ((StringHashSet.keys h) =   [|"At"; "A"; "B"; "Ba"|]);
  let h2 = StringHashSet.copy h in
  assert_bool "b" ((StringHashSet.keys h2) =   [|"At"; "A"; "B"; "Ba"|]);
  StringHashSet.add h "A";
  let h3 = StringHashSet.copy_resize h 20 in
  assert_bool "c" ((StringHashSet.keys h3) =   [|"A"; "B"; "At"; "Ba"|]);
  let h4 = StringHashSet.empty() in
  let h5 = StringHashSet.copy h4 in
  assert_bool "d" (h4 = h5); 
  assert_bool "d2" (StringHashSet.equal h4  h5); 
      
;;

let test12 () =
  let s1 = IntHashSet.create 10 0 in
  IntHashSet.add s1 1;
  IntHashSet.add s1 2;
  let s2 = IntHashSet.create 10 0 in
  IntHashSet.add s2 2;
  IntHashSet.add s2 3;
  let s3 = IntHashSet.union s1 s2 in
  assert_bool "a" (IntHashSet.mem s3 1);
  assert_bool "b" (IntHashSet.mem s3 2);
  assert_bool "c" (IntHashSet.mem s3 3);
  assert_bool "d" ((IntHashSet.length s3) = 3);
;;

let test13 () =
  (* old bug *)
  let n = 100 in
  let rand = ref 10 in
  let s = IntHashSet.create n 0 in
  for i = 0 to 66 do
    IntHashSet.add s !rand;
    rand := (!rand * 7141 + 54773) mod 259200;
  done;
  assert_bool "a" (IntHashSet.mem s 10);
  IntHashSet.add  s 16694;
  assert_bool "b" (IntHashSet.mem s 10);  
;; 

let test14 () =
  let s1 = IntHashSet.create 10 0 in
  IntHashSet.add s1 1;
  IntHashSet.add s1 2;
  IntHashSet.add s1 3;
  let s2 = IntHashSet.create 10 0 in
  IntHashSet.add s2 1;
  IntHashSet.add s2 3;
  IntHashSet.add s2 4;
  IntHashSet.add s2 5;
  let s3 = IntHashSet.diff s1 s2 in
  assert_bool "a" ((IntHashSet.keys s3) = [|2|]);
  IntHashSet.diff_update s1 s2;
  assert_bool "b" ((IntHashSet.keys s1) = [|2|]);
  ;;

let test15 () =
  let s1 = IntHashSet.create 10 0 in
  IntHashSet.add s1 1;
  IntHashSet.add s1 2;
  IntHashSet.add s1 3;
  let s2 = IntHashSet.create 10 0 in
  IntHashSet.add s2 1;
  IntHashSet.add s2 3;
  IntHashSet.add s2 4;
  IntHashSet.add s2 5;
  let s3 = IntHashSet.inter s1 s2 in
  assert_bool "a" ((IntHashSet.keys s3) = [|1;3|]);
  IntHashSet.inter_update s1 s2;
  assert_bool "b" ((IntHashSet.keys s1) = [|1;3|]);
;;

let test16 () =
    (* old bug found running ./intersect1b 30 50; see old_bugs *)
  let s1 = IntHashSet.create 39 0 in
  IntHashSet.add s1 40;
  IntHashSet.add s1 10;
  IntHashSet.add s1 49;
  IntHashSet.remove s1 40;
  assert_bool "a" (IntHashSet.mem s1 49);
;;

let test17 () =
  let s = IntHashSet.create 4 0 in
  IntHashSet.add s 1;
  IntHashSet.add s 5;
  IntHashSet.remove s 9;
  assert_bool "a" ((IntHashSet.length s) = 2);
;;

let test18 () =
  Random.set_state random_state;
  let screate nitems maxval =
  (* create dictionary of nitems with integer keys from (0,maxval) interval *)
    let s = IntHashSet.create 0 0 and
    size = ref 0 and
    rnd = ref 0 in
    while !size < nitems && !size < maxval do
      rnd := Random.int maxval;
      IntHashSet.add s !rnd;
      size := IntHashSet.length s;
    done;
    s;
    in
    let nitems = 21
    and maxval = 48 in
    let s1 = screate nitems maxval in
    let s1c = IntHashSet.copy s1 in
    assert_bool "w1" (IntHashSet.mem s1c 1);
    let s2 = screate nitems maxval in
    let s3 = IntHashSet.inter s1 s2 in
    assert_bool "a" ((IntHashSet.length s3) = 10);    
    IntHashSet.inter_update s1 s2;
    assert_bool "b" ((IntHashSet.length s1) = 10);
;;

let test19 () =
  let s1 = IntHashSet.create 0 0 and
  s2 = IntHashSet.create 10 2 in
  IntHashSet.add s1 1;
  IntHashSet.add s1 5;
  IntHashSet.add s2 5;
  IntHashSet.add s2 1;
  assert_bool "a" (IntHashSet.equal s1 s2);
  IntHashSet.add s2 2;
  assert_bool "b" (not (IntHashSet.equal s1 s2));
  let s1c = IntHashSet.copy s1 in
  assert_bool "c" (IntHashSet.equal s1c s1);
  (* but *)
  assert_bool "d" (not (IntHashSet.equal s1 s2));
;;

let test20 () =
  let s1 = IntHashSet.create 0 0 in
  IntHashSet.add s1 1;
  IntHashSet.add s1 5;
  IntHashSet.add s1 2; (* in same bucket as 5, since 5 mod 3 = 2 *)
  let sum = IntHashSet.fold (fun key accu -> key + accu) in
  assert_bool "a" ((sum s1 0) = 8);
;;
   
let test21 () =
  let s1 = IntHashSet.create 0 0 in
  IntHashSet.add s1 1;
  IntHashSet.add s1 5;
  IntHashSet.add s1 11; (* in same bucket as 5, since 11 mod 3 = 2 *)
  let odd x = (x mod 2) = 1 in
  assert_bool "a" (IntHashSet.for_all odd s1); 
  IntHashSet.add s1 4;
  assert_bool "b" (not(IntHashSet.for_all odd s1)); 
;;

let test22 () =
  let h = IntHashSet.empty() in
  assert_bool "a" ((IntHashSet.length h) = 0);
  IntHashSet.add h 1;
  assert_bool "a" ((IntHashSet.length h) = 1);
  IntHashSet.add h 5;
  IntHashSet.add h 11; (* in same bucket as 5, since 11 mod 3 = 2 *)
  let even x = (x mod 2) = 0 in
  assert_bool "a" (not (IntHashSet.exists even h)); 
  IntHashSet.add h 4;
  assert_bool "b" (IntHashSet.exists even h); 
;;


let test23 () =
  let s1 = IntHashSet.create 0 0 in
  IntHashSet.add s1 1;
  IntHashSet.add s1 5;
  IntHashSet.add s1 11;
  let s2 = IntHashSet.create 0 0 in
  IntHashSet.add s2 11;
  IntHashSet.add s2 12;
  let s3 = IntHashSet.symmetric_diff s1 s2 in
  assert_bool "a" ((IntHashSet.keys s3) = [|1; 5; 12|]);
  IntHashSet.symmetric_diff_update s1 s2;
  assert_bool "b" ((IntHashSet.keys s1) = [|1; 5; 12|]);
;;

module NthSet = HashSet.Make(struct
  type t = int * int list
  let compare (i:t) j =  (compare i j)
  let hash = Hashtbl.hash
end)

let test24 () = 
  let h = NthSet.empty() in
  NthSet.add h (8, [-1; 0; -1]);
  assert_bool "a" ((NthSet.length h) = 1);
  NthSet.add h (5, [0; 0; -1]);
  NthSet.add h (7, [-1; 0; -1]);
  NthSet.add h (6, [0; 0; -1]);
  NthSet.add h (7, [0; 0; -1]);
  NthSet.add h (6, [0; -1; 0]);
  NthSet.add h (5, [-1; 0; 0]);
  NthSet.add h (8, [0; -1; 0]);
  NthSet.add h (5, [0; 0; 0]);
  NthSet.add h (6, [0; 0; 0]);
  NthSet.add h (7, [0; 0; 0]);
  NthSet.add h (8, [0; 0; 0]);
  NthSet.add h (6, [-1; -1; -1]);
  NthSet.add h (7, [-1; -1; -1]);
  NthSet.add h (5, [0; -1; -1]);
  NthSet.add h (6, [0; -1; -1]);
  NthSet.add h (7, [0; -1; -1]);
  NthSet.add h (8, [0; -1; -1]);
  NthSet.add h (5, [-1; -1; 0]);
  NthSet.add h (6, [-1; -1; 0]);
  NthSet.add h (7, [-1; -1; 0]);
  NthSet.add h (5, [0; -1; 0]);
  NthSet.add h (8, [-1; -1; 0]);
  NthSet.add h (7, [-1; 0; 0]);
  NthSet.add h (8, [-1; 0; 0]);
  NthSet.add h (8, [-1; -1; -1]);
  NthSet.add h (5, [-1; 0; -1]);
  NthSet.add h (6, [-1; 0; -1]);
  let hc = NthSet.copy h in
  assert_bool "a" (NthSet.equal h hc);
;;


module ASet = HashSet.Make(struct
  type t = int array
  let compare (i:t) j =  (compare i j)
  let hash x = x.(0)
end)

let test25 () =
  let s = ASet.create 6 [|0;0;0;0;0;0|] in
  for i = 1 to 4 do
    ASet.add s [|i;i;0;0;0;i|];
  done;
  assert_bool "a" ((ASet.length s) = 4); 
  ASet.add s [|5;5;0;0;0;5|];
  assert_bool "b" ((ASet.length s) = 5); 
  let rb = ref true in
  for i=1 to 5 do
    rb := !rb && (ASet.mem s  [|i;i;0;0;0;i|]);
    if not (ASet.mem s  [|i;i;0;0;0;i|]) then Printf.printf "not mem, i=%d\n" i;
  done;
  assert_bool "c" (Array.length (ASet.keys s) = 5);
  assert_bool "d" !rb;
    
;;


let suite = "OUnit Example" >::: [ 
  "test1" >:: test1;
  "test2" >:: test2;
  "test3" >:: test3;
  "test4" >:: test4;
  "test5" >:: test5;
  "test6" >:: test6;
  "test7" >:: test7;
  "test8" >:: test8;
  "test9" >:: test9;
  "test10" >:: test10;
  "test11" >:: test11;
  "test12" >:: test12;
  "test13" >:: test13;
  "test14" >:: test14;
  "test15" >:: test15;
  "test16" >:: test16;
  "test17" >:: test17;
  "test18" >:: test18;
  "test19" >:: test19;
  "test20" >:: test20;
  "test21" >:: test21;
  "test22" >:: test22;
  "test23" >:: test23;
  "test24" >:: test24;
  "test25" >:: test25;
  ];;
run_test_tt_main suite;;
