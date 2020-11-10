package mua;


import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.*;

public class Interpreter_Mua {
    HashMap<String,Bool_Mua> Bool_Map;
    HashMap<String,List_Mua> List_Map;
    HashMap<String,Number_Mua> Number_Map;
    HashMap<String,Word_Mua> Word_Map;
    Scanner scan;
    public void begin()
    {
        Bool_Map = new HashMap<>();
        List_Map = new HashMap<>();
        Number_Map = new HashMap<>();
        Word_Map = new HashMap<>();
        scan = new Scanner(System.in);
        String inst = new String();
        while(scan.hasNextLine())
        {
            inst = scan.nextLine();
            if(inst.equals("")) break;
            // 改成循环
            String[] nodes = inst.replace("("," ( ").replace(")"," ) ")
                                 .replace("["," [ ").replace("]"," ] ")
                                 .replace("+", " + ").replace("-", " - ")
                                 .replace("*", " * ").replace("/", " / ")
                                 .replace("%", " % ").trim().split("\\s+");
            ArrayList<String> nodes_list = new ArrayList<String>(Arrays.asList(nodes));
            interpret(nodes_list);
        }
    }
    Value_Mua interpret(ArrayList<String> nodes)
    {
        if(nodes.isEmpty()) return new Value_Mua("");
        //thing
        if(nodes.get(0).charAt(0)==':')
        {
            nodes.set(0, nodes.get(0).substring(1));
            Value_Mua value =interpret(nodes);
            Word_Mua name = value.toWord();
            return thing_mua(name);
        }
        //number
        else if(nodes.get(0).matches("(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)"))
        {
            String temp = nodes.get(0);
            nodes.remove(0);
            return new Number_Mua(temp);
        }
        //word
        else if(nodes.get(0).charAt(0)=='\"')
        {
            String temp = nodes.get(0);
            nodes.remove(0);
            temp=temp.substring(1);
            String l = '\"' + temp;
            String v = temp;
            return new Word_Mua(l,v);
        }
        //boolean
        else if(nodes.get(0).equals("true")||nodes.get(0).equals("false"))
        {
            String temp = nodes.get(0);
            nodes.remove(0);
            return new Bool_Mua(temp);
        }
        //list
        else if(nodes.get(0).charAt(0)=='[')
        {
            Value_Mua l = build_list(nodes);
            return l;
        }
        //Infix
        else if(nodes.get(0).charAt(0)=='(')
        {
            Value_Mua l = infix(nodes);
            return l;
        }
        else
        {
            switch (nodes.get(0))
            {
                case "make" :
                {
                    String l = nodes.get(1);
                    String v = nodes.get(1).substring(1);
                    nodes.remove(0);
                    nodes.remove(0);
                    Word_Mua name = new Word_Mua(l,v);
                    Value_Mua value =interpret(nodes);
                    return make_mua(name, value);
                }
                case "thing":
                {
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes);
                    Word_Mua name = value.toWord();
                    return thing_mua(name);
                }
                case "print":
                {
                    nodes.remove(0);
                    return print_mua(interpret(nodes));
                }
                case "read":
                {
                    nodes.remove(0);
                    return read_mua();
                }
                case "add":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    return add_mua(a.toNumber(),b.toNumber());
                }
                case "sub":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    return sub_mua(a.toNumber(),b.toNumber());
                }
                case "mul":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    return mul_mua(a.toNumber(),b.toNumber());
                }
                case "div":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    return div_mua(a.toNumber(),b.toNumber());
                }
                case "mod":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    return mod_mua(a.toNumber(),b.toNumber());
                }
                case "erase":
                {
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes);
                    Word_Mua name = value.toWord();
                    return erase_mua(name);
                }
                case "isname":
                {
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes);
                    Word_Mua name = value.toWord();
                    return isname_mua(name);
                }
                case "run":
                {
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes);
                    List_Mua list = value.toList();
                    return run_mua(list);
                }
                //记得+run!
                case "eq":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = eq_mua(a.toNumber(),b.toNumber());
                    return res;
                }
                case "gt":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = gt_mua(a.toNumber(),b.toNumber());
                    return res;
                }
                case "lt":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = lt_mua(a.toNumber(),b.toNumber());
                    return res;
                }
                case "and":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = and_mua(a.toBool(),b.toBool());
                    return res;
                }
                case "or":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = or_mua(a.toBool(),b.toBool());
                    return res;
                }
                case "not":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = not_mua(a.toBool());
                    return res;
                }
                case "if":
                {
                    nodes.remove(0);
                    Value_Mua j =interpret(nodes);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = if_mua(j.toBool(),a.toList(),b.toList());
                    return res;
                }
                case "isnumber":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = isnumber_mua(a);
                    return res;
                }
                case "isword":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = isword_mua(a);
                    return res;
                }
                case "islist":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = islist_mua(a);
                    return res;
                }
                case "isbool":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = isbool_mua(a);
                    return res;
                }
                case "isempty":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = isempty_mua(a);
                    return res;
                }
                default:
                {
                    //A word
                    String temp = nodes.get(0);
                    nodes.remove(0);
                    String l = '\"' + temp;
                    return new Word_Mua(l, temp);
                }
            }
        }
    }
    Value_Mua interpret(ArrayList<String> nodes, ArrayList<Value_Mua> ress, int k)
    {
        if(nodes.isEmpty()) return new Value_Mua();
        //thing
        if(nodes.get(0).charAt(0)==':')
        {
            k++;
            nodes.set(0, nodes.get(0).substring(1));
            Value_Mua value =interpret(nodes, ress, k);
            Word_Mua name = value.toWord();
            Value_Mua res=thing_mua(name);
            ress.set(k,res);
            return res;
        }
        //number
        else if(nodes.get(0).matches("(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)"))
        {
            String temp = nodes.get(0);
            nodes.remove(0);
            return new Number_Mua(temp);
        }
        //word
        else if(nodes.get(0).charAt(0)=='\"')
        {
            String temp = nodes.get(0);
            nodes.remove(0);
            temp=temp.substring(1);
            String l = '\"' + temp;
            String v = temp;
            return new Word_Mua(l,v);
        }
        //boolean
        else if(nodes.get(0).equals("true")||nodes.get(0).equals("false"))
        {
            String temp = nodes.get(0);
            nodes.remove(0);
            return new Bool_Mua(temp);
        }
        //list
        else if(nodes.get(0).charAt(0)=='[')
        {
            String temp = nodes.get(0);
            Value_Mua l = build_list(nodes);
            return l;
        }
        else
        {
            switch (nodes.get(0))
            {
                case "make" :
                {
                    k++;
                    String l = nodes.get(1);
                    String v = nodes.get(1).substring(1);
                    nodes.remove(0);
                    nodes.remove(0);
                    Word_Mua name = new Word_Mua(l,v);
                    Value_Mua value =interpret(nodes, ress, k);
                    Value_Mua res = make_mua(name, value);
                    ress.set(k,res);
                    return res;
                }
                case "thing":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes,ress,k);
                    Word_Mua name = value.toWord();
                    Value_Mua res = thing_mua(name);
                    ress.set(k,res);
                    return res;
                }
                case "print":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua res = print_mua(interpret(nodes, ress, k));
                    ress.set(k,res);
                    return res;
                }
                case "read":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua res = read_mua();
                    ress.set(k,res);
                    return res;
                }
                case "add":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua b =interpret(nodes, ress, k);
                    Value_Mua res = add_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "sub":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua b =interpret(nodes, ress, k);
                    Value_Mua res = sub_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "mul":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua b =interpret(nodes, ress, k);
                    Value_Mua res = mul_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "div":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua b =interpret(nodes, ress, k);
                    Value_Mua res = div_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "mod":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua b =interpret(nodes, ress, k);
                    Value_Mua res = mod_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "erase":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes, ress, k);
                    Word_Mua name = value.toWord();
                    Value_Mua res = erase_mua(name);
                    ress.set(k,res);
                    return res;
                }
                case "isname":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes, ress, k);
                    Word_Mua name = value.toWord();
                    Value_Mua res = isname_mua(name);
                    ress.set(k,res);
                    return res;
                }
                case "run":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes,ress,k);
                    List_Mua list = value.toList();
                    Value_Mua res = run_mua(list);
                    ress.set(k,res);
                    return res;
                }
                case "eq":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua b =interpret(nodes, ress, k);
                    Value_Mua res = eq_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "gt":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua b =interpret(nodes, ress, k);
                    Value_Mua res = gt_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "lt":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua b =interpret(nodes, ress, k);
                    Value_Mua res = lt_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "and":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua b =interpret(nodes, ress, k);
                    Value_Mua res = and_mua(a.toBool(),b.toBool());
                    ress.set(k,res);
                    return res;
                }
                case "or":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua b =interpret(nodes, ress, k);
                    Value_Mua res = or_mua(a.toBool(),b.toBool());
                    ress.set(k,res);
                    return res;
                }
                case "not":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua res = not_mua(a.toBool());
                    ress.set(k,res);
                    return res;
                }
                case "if":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua j =interpret(nodes, ress, k);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua b =interpret(nodes, ress, k);
                    Value_Mua res = if_mua(j.toBool(),a.toList(),b.toList());
                    ress.set(k,res);
                    return res;
                }
                case "isnumber":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua res = isnumber_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "isword":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua res = isword_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "islist":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua res = islist_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "isbool":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua res = isbool_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "isempty":
                {
                    k++;
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k);
                    Value_Mua res = isempty_mua(a);
                    ress.set(k,res);
                    return res;
                }
                default:
                {
                    //A word
                    String temp = nodes.get(0);
                    nodes.remove(0);
                    String l = '\"' + temp;
                    return new Word_Mua(l, temp);
                }
            }
        }
    }
    Value_Mua build_list(ArrayList<String> nodes)
    {
        StringBuilder literal = new StringBuilder("");
        Iterator<String> iter = nodes.iterator();
        while(iter.hasNext())
        {
            String str=iter.next();
            if("[".equals(str))
            {
                literal.append("[");
                iter.remove();
            }
            else if("]".equals(str))
            {
                literal.deleteCharAt(literal.length()-1);//移除空格
                literal.append("]");
                iter.remove();
                break;
            }
            else
            {
                literal.append(str).append(" ");
                iter.remove();
            }
        }
        return new List_Mua(literal);
    }
    Number_Mua infix(ArrayList<String> nodes)
    {
        Stack<Number_Mua> num_stack = new Stack<Number_Mua>();
        Stack<infix_op> op_stack = new Stack<infix_op>();
        Iterator<String> iter = nodes.iterator();
        String str = "";
        if(iter.hasNext())
        {
            str = iter.next();
        }
        while(iter.hasNext())
        {
            //OPERATER
            if(str.matches("\\+|-|\\*|/|%|\\(|\\)"))
            {
                infix_op temp = new infix_op(str);
                if(op_stack.isEmpty())
                {
                    op_stack.push(temp);
                    iter.remove();
                    str = iter.next();
                }
                else if(op_stack.peek().in_prior<temp.out_prior)
                {
                    op_stack.push(temp);
                    iter.remove();
                    str = iter.next();
                }
                else if(op_stack.peek().in_prior==temp.out_prior)
                {
                    op_stack.pop();
                    iter.remove();
                    str = iter.next();
                }
                else
                {
                    while((!op_stack.isEmpty())&&op_stack.peek().in_prior>temp.out_prior)
                    {
                        Number_Mua b = num_stack.pop();
                        Number_Mua a = num_stack.pop();
                        num_stack.push(op_stack.pop().exe(a,b));
                    }
                }
            }
            //NUMBER
            else if(str.matches("(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)"))
            {
                num_stack.push(new Number_Mua(str));
                iter.remove();
                str = iter.next();
            }
            //PREFIX OP
            else
            {
                switch (str)
                {
                    case "add":
                    {
                        iter.remove();
                        str= iter.next();
                        Value_Mua a =interpret(nodes);
                        Value_Mua b =interpret(nodes);
                        num_stack.push(add_mua(a.toNumber(),b.toNumber()));
                    }
                    case "sub":
                    {
                        iter.remove();
                        str= iter.next();
                        Value_Mua a =interpret(nodes);
                        Value_Mua b =interpret(nodes);
                        num_stack.push(sub_mua(a.toNumber(),b.toNumber()));
                    }
                    case "mul":
                    {
                        iter.remove();
                        str= iter.next();
                        Value_Mua a =interpret(nodes);
                        Value_Mua b =interpret(nodes);
                        num_stack.push(mul_mua(a.toNumber(),b.toNumber()));
                    }
                    case "div":
                    {
                        iter.remove();
                        str= iter.next();
                        Value_Mua a =interpret(nodes);
                        Value_Mua b =interpret(nodes);
                        num_stack.push(add_mua(a.toNumber(),b.toNumber()));
                    }
                    case "mod":
                    {
                        iter.remove();
                        str= iter.next();
                        Value_Mua a =interpret(nodes);
                        Value_Mua b =interpret(nodes);
                        num_stack.push(add_mua(a.toNumber(),b.toNumber()));
                    }
                }
            }
        }
       // THE LAST ")"
        while((op_stack.peek().op!= infix_op.INFIX_OP.LEFT))
        {
            Number_Mua b = num_stack.pop();
            Number_Mua a = num_stack.pop();
            num_stack.push(op_stack.pop().exe(a,b));
        }
        return num_stack.pop();
    }
    Value_Mua make_mua(Word_Mua name, Value_Mua value)
    {
        switch(value.Type_Mua)
        {
            case WORD:Word_Map.put(name.word_value.toString(),new Word_Mua(value));break;
            case LIST:List_Map.put(name.word_value.toString(),new List_Mua(value));break;
            case BOOL:Bool_Map.put(name.word_value.toString(), new Bool_Mua(value));break;
            case NUMBER:Number_Map.put(name.word_value.toString(), new Number_Mua(value));break;
            default:Word_Map.put(name.word_value.toString(),new Word_Mua(value));break;
        }
        return value;
    }
    Value_Mua thing_mua(Word_Mua word_name)
    {
        String name = word_name.word_value.toString();
        if(Word_Map.containsKey(name)) return Word_Map.get(name);
        else if(Number_Map.containsKey(name)) return Number_Map.get(name);
        else if(Bool_Map.containsKey(name)) return Bool_Map.get(name);
        else if(List_Map.containsKey(name)) return List_Map.get(name);
        else return new Value_Mua();
    }
    Value_Mua print_mua(Value_Mua value)
    {
        String str;
        switch(value.Type_Mua)
        {
            case WORD:str = value.literal.substring(1);break;
            case LIST:str = value.literal.substring(1,value.literal.length()-2);break;
            case BOOL:str = value.literal;break;
            case NUMBER:str = value.literal;break;
            default:str="";break;
        }
        System.out.println(str);
        return value;
    }
    Value_Mua read_mua()
    {
        String str = "";
        Value_Mua v;
        if (scan.hasNextLine())
        {
            str = scan.nextLine();
        }
        String judge = "(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)";
        if(str.matches(judge))
        {
            v = new Value_Mua(str);
            v.Type_Mua= Value_Mua.TYPE_MUA.NUMBER;
        }
        else
        {
            v = new Value_Mua('\"'+str);
            v.Type_Mua= Value_Mua.TYPE_MUA.WORD;
        }
        return v;
    }
    Value_Mua erase_mua(Word_Mua word_name)
    {
        String name = word_name.word_value.toString();
        if(Word_Map.containsKey(name)) return Word_Map.remove(name);
        else if(Number_Map.containsKey(name)) return Number_Map.remove(name);
        else if(Bool_Map.containsKey(name)) return Bool_Map.remove(name);
        else if(List_Map.containsKey(name)) return List_Map.remove(name);
        else return new Value_Mua();
    }
    Bool_Mua isname_mua(Word_Mua word_name)
    {
        String name = word_name.word_value.toString();
        if(Word_Map.containsKey(name)) return new Bool_Mua(true);
        else if(Number_Map.containsKey(name)) return new Bool_Mua(true);
        else if(Bool_Map.containsKey(name)) return new Bool_Mua(true);
        else if(List_Map.containsKey(name)) return new Bool_Mua(true);
        else return new Bool_Mua(false);
    }
    Value_Mua run_mua(List_Mua list)
    {
        ArrayList<Value_Mua> ress = new ArrayList<Value_Mua>();
        int k=0;
        interpret(list.list_value, ress, k);
        return ress.get(ress.size()-1);
    }
    Number_Mua add_mua(Number_Mua a, Number_Mua b)
    {
        return new Number_Mua(a.number_value+b.number_value);
    }
    Number_Mua sub_mua(Number_Mua a, Number_Mua b)
    {
        return new Number_Mua(a.number_value-b.number_value);
    }
    Number_Mua mul_mua(Number_Mua a, Number_Mua b)
    {
        return new Number_Mua(a.number_value*b.number_value);
    }
    Number_Mua div_mua(Number_Mua a, Number_Mua b)
    {
        return new Number_Mua(a.number_value/b.number_value);
    }
    Number_Mua mod_mua(Number_Mua a, Number_Mua b)
    {
        return new Number_Mua(a.number_value%b.number_value);
    }
    Bool_Mua gt_mua(Number_Mua a, Number_Mua b)
    {
        return new Bool_Mua(a.number_value>b.number_value);
    }
    Bool_Mua eq_mua(Number_Mua a, Number_Mua b)
    {
        return new Bool_Mua(a.number_value==b.number_value);
    }
    Bool_Mua lt_mua(Number_Mua a, Number_Mua b)
    {
        return new Bool_Mua(a.number_value<b.number_value);
    }
    Bool_Mua and_mua(Bool_Mua a, Bool_Mua b)
    {
        return new Bool_Mua(a.bool_value&&b.bool_value);
    }
    Bool_Mua or_mua(Bool_Mua a, Bool_Mua b)
    {
        return new Bool_Mua(a.bool_value||b.bool_value);
    }
    Bool_Mua not_mua(Bool_Mua a)
    {
        return new Bool_Mua(!a.bool_value);
    }
    Value_Mua if_mua(Bool_Mua j, List_Mua a, List_Mua b)
    {
        Value_Mua res;
        if(j.bool_value)
        {
            res = interpret(a.list_value);
        }
        else
        {
            res = interpret(b.list_value);
        }
        if(res.literal.equals("")) return new List_Mua("");
        return res;
    }
    Bool_Mua isnumber_mua(Value_Mua v)
    {
        return v.isnumber();
    }
    Bool_Mua isword_mua(Value_Mua v)
    {
        return v.isword();
    }
    Bool_Mua islist_mua(Value_Mua v)
    {
        return v.islist();
    }
    Bool_Mua isbool_mua(Value_Mua v)
    {
        return v.isbool();
    }
    Bool_Mua isempty_mua(Value_Mua v)
    {
        return v.isempty();
    }
}
