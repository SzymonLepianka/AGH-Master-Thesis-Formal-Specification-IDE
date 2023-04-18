grammar Java;
prule: creators;
creators:  seq | alt| branchRe | concurRe | cond | para | loop | choice | seqSeq | repeat ;
seq: seqoptions;
seqoptions:seqBranch|seqConcur|seqPrime;
seqPrime:'Seq' twoArguments;
seqBranch:'Seq''('branch ','branchRe')';
seqConcur:'Seq''('concur ','concurRe')';
branch: 'Branch' threeArguments;
branchRe: 'BranchRe' threeArguments;
concur: 'Concur' threeArguments;
concurRe: 'ConcurRe' threeArguments;
cond: 'Cond' fourArguments;
para: 'Para' fourArguments;
loop: 'Loop' fourArguments;
choice: 'Choice' fourArguments;
seqSeq: 'SeqSeq' threeArguments;
repeat: 'Repeat' fourArguments;
alt: 'Alt' twoArguments;
twoArguments : '(' arg_java ',' arg_java ')';
threeArguments : '(' arg_java ',' arg_java ',' arg_java ')';
fourArguments : '(' arg_java ',' arg_java ',' arg_java ',' arg_java ')';
function: CharArray '(' (arg_java',')*(arg_java)* ')';
arg_java:  prule | function | string |special_String;
special_String:'#'CharArray'#';
string: CharArray;
CharArray: [a-zA-Z0-9_]+ [a-zA-Z0-9_+ =!<>%?]*;