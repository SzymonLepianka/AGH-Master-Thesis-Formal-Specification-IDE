open OUnit
open Dumper
open HashSet

let random_state =  Random.get_state();;

let test1 () =
  let h = create 10 0 in
  add h 0;
  add h 1;
  assert_bool "a" (mem h 1);
  assert_bool "b" (not (mem h 2) );
  remove h 0;
  assert_bool "c" (not (mem h 0) );
  add h 12;
  add h 2;
  assert_bool "d" (mem h 2);
  (* bucket occupations 0,1,2,0,0,..0; variance([1,2]) = 0.25  *)
  (*
  assert_bool "e" ((0.24999 < (bucket_var h)) && ((bucket_var h)< 0.250001));
  *)
  assert_bool "e" ((keys h) = [|1;2;12|]);
  assert_bool "f" ((bucket_lengths h) = [|1;2|]);
;;
  
let test1a () =
  let h = create 10 0. in
  add h 0.1;
  add h 1.2;
  assert_bool "a" (mem h 1.2);
  assert_bool "b" (not (mem h 2.) );
  remove h 0.1;
  assert_bool "c" (not (mem h 0.1) );
  add h 12.3;
  add h 2.4;
  assert_bool "d" (mem h 2.4);
(*
  prerr_endline (dump (keys h));
  prerr_endline (dump [|1.1; 2.3|]);
  dump: impossible tag (254)
  *)
  assert_bool "f" ((keys h) = [|12.3; 1.2; 2.4|]);
  assert_bool "g" ((bucket_lengths h) = [|1;1;1|]);
;;
  
let test2 () =
  let h1 = create 10 "" in
  add h1 "A";
  add h1 "B";
  assert_bool "a" ((keys h1) = [|"A";"B"|]);
  add h1 "B";
  assert_bool "b" ((keys h1) = [|"A";"B"|]);
;;

let test3 () =
  let h = create 10 0 in
  add h 0;
  add h 1;
  remove h 0;
  assert_bool "a" ((keys h) = [|1|]);;
 
let test3a () =
  let h = create 10 0. in
  add h 0.3;
  add h 1.;
  remove h 0.3;
  assert_bool "a" ((keys h) = [|1.0|]);;
 
let test4 () =
  let h = create 10 "" in
  add h "A";
  add h "B";
  add h "At";
  add h "Ba";
  (* "At" and "Ba" have the same hash; "Ba" is in the table; see readme *)
  assert_bool "a" (mem h "Ba");
  assert_bool "b" ((keys h) =  [|"At"; "A"; "B"; "Ba"|]);
  add h "Ba";
  assert_bool "c" ((length h) = 4);
  assert_bool "d" (mem h "Ba");
  add h "Bt";
  add h "Ca";
  assert_bool "e" ((keys h) = [|"Bt"; "At"; "A"; "B"; "Ca"; "Ba"|]);
  add h "Da";
  add h "Ct";
  assert_bool "f" (mem h "Ct");
;;

let test5 () =
  let h = create 10 ""in
  add h "A";
  add h "B";
  add h "At";
  add h "Ba";
  (* "At" and "Ba" have the same hash; "Ba" is in the table; see readme *)
  assert_bool "a" ((length h) = 4);
  assert_bool "a1" (mem h "At");
  remove h "Ba";
  assert_bool "b1" (mem h "At");
  assert_bool "b" ((length h) = 3); 
  add h "Ba";
  assert_bool "b2" (mem h "At");
  assert_bool "c" ((length h) = 4); 
  remove h "At";
  assert_bool "d" ((length h) = 3); 
;;

  
let test6 () =
  let h = create 4 ""in
  assert_bool "a" ((length h) = 0);
  add h "AtAtAtAt";
  add h "AtAtAtBa";
  add h "AtAtBaAt";
  add h "AtAtBaBa";
  add h "AtBaAtAt";
  add h "AtBaAtBa";
  add h "AtBaBaAt";
  add h "AtBaBaBa";
  add h "BaAtAtAt";
  add h "BaAtAtBa";
  add h "BaAtBaAt";
  assert_bool "b" ((length h) = 11); 
;; 
 

let test7 () =
  let h = create 4 0 in
  assert_bool "a" ((length h) = 0); 
  for i=0 to 10 do
    add h i;
  done;
  assert_bool "b" ((length h) = 11); 

  for i=11 to 14 do
    add h i;
  done;
  assert_bool "d" ((length h) = 15); 
;;

let test8 () =
  let n = 10 in
  let my_hash = create n 0 in
  
  for i=1 to n do
      add my_hash i;
  done;
  assert_bool "a" ((length my_hash) = 10);
  for i=1 to n do
      add my_hash i;
  done;
  assert_bool "b" ((length my_hash) = 10);
;;

let test8a () =
  let n = 10 in
  let my_hash = create n 0. in
  
  for i=1 to n do
      add my_hash ((float i) +. 0.23);
  done;
  assert_bool "a" ((length my_hash) = 10);
  for i=1 to n do
      add my_hash ((float i) +. 0.23);
  done;
  assert_bool "b" ((length my_hash) = 10);
;;

let test9 () =
  let h = create 10 "" in
  add h "A";
  add h "B";
  add h "At";
  add h "Ba";
  let a = ref [] in
  iter (fun k -> (a := k :: !a)) h;
  assert_bool "a" (!a =  ["Ba"; "B" ; "A"; "At"]);
  assert_bool "b" ((length h) = 4); 
  clear h;
  assert_bool "c" ((length h) = 0); 
  assert_bool "d" ((keys h) = [| |]);
;;  

let test10 () =
  let h = create 10 "" in
  add h "A";
  add h "B";
  add h "At";
  add h "Ba";
  let h2 = create 4 "" in
  add h2 "B";
  add h2 "Ba";
  add h2 "C";
  let h3 = union h h2 in
  assert_bool "a" ((keys h3) =    [|"At"; "A"; "B"; "C"; "Ba"|]);
  update h h2;
  assert_bool "b" ((keys h) =    [|"At"; "A"; "B"; "C"; "Ba"|]);
;;  


let test11 () =
  let h = create 10 "" in
  add h "A";
  add h "B";
  add h "At";
  add h "Ba";
  assert_bool "a" ((keys h) =   [|"At"; "A"; "B"; "Ba"|]);
  let h2 = copy h in
  assert_bool "b" ((keys h2) =   [|"At"; "A"; "B"; "Ba"|]);
  add h "A";
  let h3 = copy_resize h 20 in
  assert_bool "c" ((keys h3) =   [|"A"; "B"; "At"; "Ba"|]);
  let h4 = empty() in
  let h5 = copy h4 in
  assert_bool "d" (h4 = h5);
       
;;

let test12 () =
  let s1 = create 10 0 in
  add s1 1;
  add s1 2;
  let s2 = create 10 0 in
  add s2 2;
  add s2 3;
  let s3 = union s1 s2 in
  assert_bool "a" (mem s3 1);
  assert_bool "b" (mem s3 2);
  assert_bool "c" (mem s3 3);
  assert_bool "d" ((length s3) = 3);
;;

let test13 () =
  (* old bug *)
  let n = 100 in
  let rand = ref 10 in
  let s = create n 0 in
  for i = 0 to 66 do
    add s !rand;
    rand := (!rand * 7141 + 54773) mod 259200;
  done;
  assert_bool "a" (mem s 10);
  add  s 16694;
  assert_bool "b" (mem s 10);  
;; 

let test13a () =
  (* old bug *)
  let n = 100 in
  let rand = ref 10 in
  let s = create n 0. in
  for i = 0 to 66 do
    add s (float !rand);
    rand := (!rand * 7141 + 54773) mod 259200;
  done;
  assert_bool "a" (mem s 10.);
  add  s 16694.;
  assert_bool "b" (mem s 10.);  
;; 

let test14 () =
  let s1 = create 10 0 in
  add s1 1;
  add s1 2;
  add s1 3;
  let s2 = create 10 0 in
  add s2 1;
  add s2 3;
  add s2 4;
  add s2 5;
  let s3 = diff s1 s2 in
  assert_bool "a" ((keys s3) = [|2|]);
  diff_update s1 s2;
  assert_bool "b" ((keys s1) = [|2|]);
  ;;

let test14a () =
  let s1 = create 10 0.0 in
  add s1 1.1;
  add s1 2.2;
  add s1 3.3;
  let s2 = create 10 0. in
  add s2 1.1;
  add s2 3.3;
  add s2 4.4;
  add s2 5.5;
  let s3 = diff s1 s2 in
  assert_bool "a" ((keys s3) = [|2.2|]);
  diff_update s1 s2;
  assert_bool "b" ((keys s1) = [|2.2|]);
  ;;

let test15 () =
  let s1 = create 10 0 in
  add s1 1;
  add s1 2;
  add s1 3;
  let s2 = create 10 0 in
  add s2 1;
  add s2 3;
  add s2 4;
  add s2 5;
  let s3 = inter s1 s2 in
  assert_bool "a" ((keys s3) = [|1;3|]);
  inter_update s1 s2;
  assert_bool "b" ((keys s1) = [|1;3|]);
;;

let test15a () =
  let s1 = create 10 0. in
  add s1 1.1;
  add s1 2.2;
  add s1 3.3;
  let s2 = create 10 0. in
  add s2 1.1;
  add s2 3.3;
  add s2 4.4;
  add s2 5.5;
  let s3 = inter s1 s2 in
  assert_bool "a" ((keys s3) = [|1.1;3.3|]);
  inter_update s1 s2;
  assert_bool "b" ((keys s1) = [|1.1;3.3|]);
;;

let test16 () =
  let s1 = create 39 0 in
  add s1 40;
  add s1 10;
  add s1 49;
  remove s1 40;
  assert_bool "a" (mem s1 49);
;;

let test17 () =
  let s = create 4 0 in
  add s 1;
  add s 5;
  remove s 9;
  assert_bool "a" ((length s) = 2);
;;

let test18 () =
  Random.set_state random_state;
  let screate nitems maxval =
  (* create dictionary of nitems with integer keys from (0,maxval) interval *)
    let s = create 0 0 and
    size = ref 0 and
    rnd = ref 0 in
    while !size < nitems && !size < maxval do
      rnd := Random.int maxval;
      add s !rnd;
      size := length s;
    done;
    s;
    in
    let nitems = 21
    and maxval = 48 in
    let s1 = screate nitems maxval in
    let s1c = copy s1 in
    assert_bool "w1" (mem s1c 1);
    let s2 = screate nitems maxval in
    let s3 = inter s1 s2 in
    assert_bool "a" ((length s3) = 10);
    inter_update s1 s2;
    assert_bool "b" ((length s1) = 10);
;;

let test19 () =
  let s1 = create 0 0 and
  s2 = create 10 2 in
  add s1 1;
  add s1 5;
  add s2 5;
  add s2 1;
  assert_bool "a" (equal s1 s2);
  add s2 2;
  assert_bool "b" (not (equal s1 s2));
  let s1c = copy s1 in
  assert_bool "c" (equal s1c s1);
  (* but *)
  assert_bool "d" (not (equal s1  s2));
;;

let test19a () =
  let s1 = create 0 0. and
  s2 = create 10 2. in
  add s1 1.1;
  add s1 5.5;
  add s2 5.5;
  add s2 1.1;
  assert_bool "a" (equal s1 s2);
  add s2 2.2;
  assert_bool "b" (not (equal s1 s2));
  let s1c = copy s1 in
  assert_bool "c" (s1c = s1);
  (* but *)
  assert_bool "d" (not (s1 = s2));
;;
let test20 () =
  let s1 = create 0 0 in
  add s1 1;
  add s1 5;
  add s1 2; (* in same bucket as 5, since 5 mod 3 = 2 *)
  let sum = fold (fun key accu -> key + accu) in
  assert_bool "a" ((sum s1 0) = 8);
;;
   
let test21 () =
  let s1 = create 0 0 in
  add s1 1;
  add s1 5;
  add s1 11; (* in same bucket as 5, since 11 mod 3 = 2 *)
  let odd x = (x mod 2) = 1 in
  assert_bool "a" (for_all odd s1); 
  add s1 4;
  assert_bool "b" (not(for_all odd s1)); 
;;

let test22 () =
  let h = empty() in
  (* let h = create 0 0 in *)
  assert_bool "a" ((length h) = 0);
  add h 1;
  assert_bool "b" ((length h) = 1);
  add h 5;
  add h 11; (* in same bucket as 5, since 11 mod 3 = 2 *)
  let even x = (x mod 2) = 0 in
  assert_bool "c" (not (exists even h)); 
  add h 4;
  assert_bool "d" (exists even h); 
;;


let test23 () =
  let s1 = create 0 0 in
  add s1 1;
  add s1 5;
  add s1 11;
  let s2 = create 0 0 in
  add s2 11;
  add s2 12;
  let s3 = symmetric_diff s1 s2 in
  assert_bool "a" ((keys s3) = [|1; 5; 12|]);
  symmetric_diff_update s1 s2;
  assert_bool "b" ((keys s1) = [|1; 5; 12|]);
;;

let test24 () = 
  let h = empty() in
  add h (8, [-1; 0; -1]);
  assert_bool "a" ((length h) = 1);
  add h (5, [0; 0; -1]);
  add h (7, [-1; 0; -1]);
  add h (6, [0; 0; -1]);
  add h (7, [0; 0; -1]);
  add h (6, [0; -1; 0]);
  add h (5, [-1; 0; 0]);
  add h (8, [0; -1; 0]);
  add h (5, [0; 0; 0]);
  add h (6, [0; 0; 0]);
  add h (7, [0; 0; 0]);
  add h (8, [0; 0; 0]);
  add h (6, [-1; -1; -1]);
  add h (7, [-1; -1; -1]);
  add h (5, [0; -1; -1]);
  add h (6, [0; -1; -1]);
  add h (7, [0; -1; -1]);
  add h (8, [0; -1; -1]);
  add h (5, [-1; -1; 0]);
  add h (6, [-1; -1; 0]);
  add h (7, [-1; -1; 0]);
  add h (5, [0; -1; 0]);
  add h (8, [-1; -1; 0]);
  add h (7, [-1; 0; 0]);
  add h (8, [-1; 0; 0]);
  add h (8, [-1; -1; -1]);
  add h (5, [-1; 0; -1]);
  add h (6, [-1; 0; -1]);
  let hc = copy h in
  assert_bool "b" (equal h hc);
;;

let create_from_list lst =
  let h = empty() in
  List.iter (fun x -> add h x) lst;
  h

let test25 () =
  let h1 = create_from_list [1;2;3;4;5;6;7;8] in
  assert_bool "a" ((length h1) = 8);
  assert_bool "b" ((keys h1) =  [|1; 2; 3; 4; 5; 6; 7; 8|]);
  let h2 = create_from_list [5;6;7;8;9;4;10] in
  let h3 = create_from_list [11;1;3;6;7;5;8;8;9;4;10] in
  inter_update h2 h1;
  assert_bool "c" ((keys h2) =  [|7; 8; 4; 5; 6|]);
  inter_update h2 h3;
  assert_bool "d" ((keys h2) =  [|7; 8; 4; 5; 6|]);
;;

let test26 () =
  let s = create 100 [0;0;0;0;0;0;0;0;0;0] in
  for i = 0 to 99 do
    add s [i;0;0;0;0;0;0;0;0;0];
  done;
  assert_bool "a" ((length s) = 100);
;;

let test27 () =
  (* done with limit_list = 3 *)
  let s = create 6 [0;0;0;0;0;0] in
  for i = 1 to 4 do
    add s [i;0;0;0;0;0];
  done;
  assert_bool "a" ((length s) = 4); (* s.data.(0) is a list *)
  add s [5;0;0;0;0;0];
  assert_bool "b" ((length s) = 5); (* s.data.(0) is a tree *)
  assert_bool "c" (mem s [5;0;0;0;0;0]);
;;  
  
let suite = "OUnit Example" >::: [ 
  "test1" >:: test1;
  "test1a" >:: test1a;
  "test2" >:: test2;
  "test3" >:: test3;
  "test3a" >:: test3a;
  "test4" >:: test4;
  "test5" >:: test5;
  "test6" >:: test6;
  "test7" >:: test7;
  "test8" >:: test8;
  "test8a" >:: test8a;
  "test9" >:: test9;
  "test10" >:: test10;
  "test11" >:: test11;
  "test12" >:: test12;
  "test13" >:: test13;
  "test13a" >:: test13a;
  "test14" >:: test14;
  "test14a" >:: test14a;
  "test15" >:: test15;
  "test15a" >:: test15a;
  "test16" >:: test16;
  "test17" >:: test17;
  "test18" >:: test18;
  "test19" >:: test19;
  "test19a" >:: test19a;
  "test20" >:: test20;
  "test21" >:: test21;
  "test22" >:: test22;
  "test23" >:: test23;
  "test24" >:: test24;
  "test25" >:: test25;
  "test26" >:: test26;
  "test27" >:: test27;
  ];;
run_test_tt_main suite;;
