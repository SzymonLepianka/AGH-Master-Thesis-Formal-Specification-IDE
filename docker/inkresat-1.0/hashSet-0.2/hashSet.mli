type 'a t
(** The type of setHash of type ['a]. *)

val empty : unit -> 'a t
(** [SetHash.empty()] creates an empty set of zero size. *)

val create : int -> 'a -> 'a t
(** [SetHash.create n k] creates a new, empty set with
   initial size [n]. For best results, [n] should be on the
   order of the expected number of elements that will be in
   the table.  The table grows as needed, so [n] is just an
   initial guess.
   [k] is a key of type ['a] which must be provided to
   initialize the set; it is for internal use; it cannot
   be accessed.  *)

val length : 'a t -> int
(** [SetHash.length h] returns the number of elements
   in the set. *)

val capacity : 'a t -> int
(** [SetHash.capacity is the size of the set, i.e. the number 
    of elements which the set can contain before resizing when 
    adding a new element. *)

val clear : 'a t -> unit
(** Empty a set. *)

val copy : 'a t -> 'a t
(** [SetHash.copy h] returns a copy of h *)

val mem : 'a t -> 'a -> bool
(** [SetHash.mem h x] checks if [x] is a member of [h]. *)

val iter : ('a -> unit) -> 'a t -> unit
(** [SetHash.iter f h] applies [f] to all elements of
    the set. The order in which the elements are passed 
    to [f] is unspecified. *)

val add_unsafe : 'a t -> 'a -> unit
(** [SetHash.add_unsafe h x] adds [x] to the set [h],
    without resizing the set. If the number of
    elements is much more than the size of the set,
    performance is poor.  *)

val copy_resize : 'a t -> int -> 'a t
(** [SetHash.copy_resize h n] return a new set
    with the same elements as [h] and with size [n] *)

val add : 'a t -> 'a -> unit
(** [SetHash.add h x] adds [x] to the set [h],
    resizing the set when the number of elements is
    equal or more than the size of the set. *)

val remove : 'a t -> 'a -> unit
(** [SetHash.remove h x] removes the element [x] from
    the set [h]. Often the element is still in the set,
    but it is not accessible. Furthermore, the size of
    the set does not change. *)

val create_from_list : 'a list -> 'a t

val bucket_lengths : 'a t -> int array

val fold : ('a -> 'b -> 'b) -> 'a t -> 'b -> 'b
(** [SetHash.fold f h a] computes [(f xN ... (f x2 (f x1 a))...)],
    where [x1 ... xN] are the elements in [s].
    The order in which the elements are passed to [f] is unspecified. *)


val for_all : ('a -> bool) -> 'a t -> bool
(** [SetHash.for_all p h] checks if all elements of the set [h]
    satisfy the predicate [p]. *)


val exists : ('a -> bool) -> 'a t -> bool
(** [SetHash.exists p h] checks if at least one element of
    the set satisfies the predicate [p]. *)


val keys : 'a t -> 'a array
(** [SetHash.keys h] returns an array with the elements of the set
    in unspecified order. *)

val equal : 'a t -> 'a t -> bool
(** [SetHash.equal h1 h2]  tests whether the sets [h1] and [h2] are
   equal, that is, contain equal elements. *)

val update : 'a t -> 'a t -> unit
val diff_update : 'a t -> 'a t -> unit
val symmetric_diff_update : 'a t -> 'a t -> unit
val inter_update : 'a t -> 'a t -> unit
val union : 'a t -> 'a t -> 'a t
val diff : 'a t -> 'a t -> 'a t
val symmetric_diff : 'a t -> 'a t -> 'a t
val inter : 'a t -> 'a t -> 'a t


(** Functorial interface *)

module type HashedType =
  sig 
    type t 
      (** The type of the elements. *)
    val compare : t -> t -> int 
      (** The comparison function used to compare elements. *)
    val hash : t -> int 
     (** A hashing function on elements. It must be such that if two elements 
      are equal according to [compare], then they have identical hash
      values as computed by [hash]. *)
  end

(* The input signature of the functor {!Hashset.Make}. *)
module type S =
  sig
    type elt
    type t
    val empty : unit -> t
    val create : int -> elt -> t
    val length : t -> int
    val capacity : t -> int
    val mem : t -> elt -> bool
    val clear : t -> unit
    val copy : t -> t
    val iter_v : (elt -> unit) -> t -> unit
    val iter : (elt -> unit) -> t -> unit
    val add_unsafe : t -> elt -> unit
    val copy_resize : t -> int -> t
    val resize : t -> elt -> unit
    val add : t -> elt -> unit
    val remove : t -> elt -> unit
    val create_from_list : elt list -> t
    val bucket_lengths : t -> int array
    val fold : (elt -> 'a -> 'a) -> t -> 'a -> 'a
    val for_all : (elt -> bool) -> t -> bool
    val exists : (elt -> bool) -> t -> bool
    val keys : t -> elt array
    val equal : t -> t -> bool
    val update : t -> t -> unit
    val diff_update : t -> t -> unit
    val symmetric_diff_update : t -> t -> unit
    val inter_update : t -> t -> unit
    val union : t -> t -> t
    val diff : t -> t -> t
    val symmetric_diff : t -> t -> t
    val inter : t -> t -> t
  end


module Make (H : HashedType) : S with type elt = H.t
(** Functor building an implementation of the hashSet structure.
    The functor [HashSet.Make] returns a structure containing
    a type [elt] of elements and a type [t] of hash sets.
    The operations perform similarly to those of the generic
    interface, but use the hashing and compare functions
    specified in the functor argument [H] instead of generic
    Pervasives.compare  and Hashtbl.hash . *)

