open HashSet;;
let m, n, i = match Sys.argv with
    [|_; m; n; i|] -> int_of_string m, int_of_string n, int_of_string i
    | _ -> invalid_arg ("Usage: nth <m> <n> <i>\n"
    ^"m = number of repetitions\n"
    ^"n = number of elements added\n"
    ^"i = max value of the random number\n")
;;


let rec random_set set n rand =
 if n=0 then () else
   begin
   add_unsafe set (rand ());
   random_set set (n - 1) rand;
   end;;

let tot_time_create = ref 0.;;
let tot_time_add = ref 0.;;
let tot_time_mem = ref 0.;;
let tot_time_remove = ref 0.;;

let t0 =  Unix.gettimeofday() in
let s = create n 0 in
let t1 =  Unix.gettimeofday() in
Printf.printf "timing: create %.3f, " (t1 -. t0);
for r=0 to m-1 do
  let t0 =  Unix.gettimeofday() in
  if r <> 0 then clear s;
  let t1 =  Unix.gettimeofday()in
  random_set s n (fun () -> Random.int i);
  let t2 =  Unix.gettimeofday() in
  tot_time_create := !tot_time_create +. t1 -. t0;
  tot_time_add := !tot_time_add +. t2 -. t1;
  let count = ref 0 in
  for j=0 to n-1 do
    if mem s j then count := !count + 1;
  done;
  let t3 =  Unix.gettimeofday() in
  tot_time_mem := !tot_time_mem +. t3 -. t2;
  (* Printf.printf "count = %d, length=%d\n" !count (length s); *)
  for j=0 to n-1 do
    remove s j;
  done;
  let t4 =  Unix.gettimeofday() in
  tot_time_remove := !tot_time_remove +. t4 -. t3;
done;

Printf.printf "clear %.3f, add %.3f, mem %.3f, remove %.3f\n" 
!tot_time_create !tot_time_add !tot_time_mem !tot_time_remove;;
