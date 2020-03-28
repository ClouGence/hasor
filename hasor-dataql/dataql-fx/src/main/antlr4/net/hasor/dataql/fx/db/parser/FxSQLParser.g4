// Define a grammar called Hello
parser grammar FxSQLParser;
options { tokenVocab = FxSQLLexer; }

rootInstSet : charA ognlExpr* charB EOF;
ognlExpr    : expr charB;
expr        : (ORI_BEGIN | SAF_BEGIN) OGNL_HAFSTR* CLOS_TAG;
charA       : CHAR*;
charB       : CHAR*;