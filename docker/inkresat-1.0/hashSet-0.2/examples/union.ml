(* example adapted from
 * http://groups.google.com/group/comp.lang.functional/msg/cdae678b2d6e7ae6
 *)
open HashSet;;
let rn = ref 0;;
let () = match Sys.argv with
  [| _; s |] ->
    rn := int_of_string s;
  | _ -> output_string stderr "Usage: union <n>\n"

let n = !rn;;


let rec random_set set n rand =
 if n=0 then () else
   begin
   add set (rand ());
   random_set set (n - 1) rand;
   end;;

let s1 = create n 0;; 
random_set s1 n (fun () -> Random.int n);;

let aux rand =
  let rs2 =  ref (create n 0) in
  random_set !rs2 n  rand;
  let t =  Sys.time() in
  let s3 = union s1 !rs2 in
  let t =  Sys.time() -. t in
  Printf.printf "%d U %d = %d %fs\n" (length s1) (length !rs2) (length s3) t ;
  in
  List.iter aux [(fun () -> Random.int n);
       (fun () -> n/2 + Random.int n);
       (fun () -> n + Random.int n); ];;
