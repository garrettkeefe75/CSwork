package assignment07;

import static org.junit.Assert.*;

import org.junit.Test;

import components.list.List;

public class SpellCheckerTest
{
	@Test
	public void testInput1()
	{
		SpellChecker sc = new BSTSpellChecker();
		sc.loadValidWords("data/a07-valid-words1.txt");
		List<String> result = sc.misspelledWords("data/a07-input1.txt");
		assertEquals("[Hello,there,,world.,,Nice,you.]", result.toString());
	}

	@Test
	public void testInput2()
	{
		SpellChecker sc = new BSTSpellChecker();
		sc.loadValidWords("data/a07-valid-words1.txt");
		List<String> result = sc.misspelledWords("data/a07-input2.txt");
		assertEquals("[Good,Assignment,8!,,Binary,structures.,,Searches,(if,BST,bad).,,Be,careful,,tricky.]",
				result.toString());
	}

	@Test
	public void testInput3()
	{
		//Ignore this test, we were just having fun
		SpellChecker sc = new BSTSpellChecker();
		sc.loadValidWords("data/a07-valid-words1.txt");
		List<String> result = sc.misspelledWords("data/The Cat in the Hat");
		assertEquals("[The,Cat,Hat,,By,Dr.,Seuss,,The,shine.," + "It,was,wet,play.,So,we,sat,All,that,cold,"
				+ ",cold,,wet,day.,,I,sat,Sally.,We,sat,there," + ",we,two.,And,I,said,,\"How,I,We,something,do!\","
				+ ",Too,wet,out,And,cold,play,ball.,So,we,sat,house."
				+ ",We,nothing,at,all.,,So,all,we,could,do,was,,Sit!,"
				+ "Sit!,Sit!,Sit!,,And,we,it.,Not,little,bit.,,BUMP!,"
				+ ",And,then,something,went,BUMP!,How,that,bump,us,jump!,"
				+ ",We,looked!,Then,we,saw,him,step,mat!,We,looked!,And,"
				+ "we,saw,him!,The,Cat,Hat!,And,he,us,,\"Why,do,sit,that?\""
				+ ",\"I,it,wet,And,sunny.,But,we,have,Lots,of,fun,that,funny!\","
				+ ",\"I,some,we,could,play,\",Said,cat.,\"I,some,new,tricks,\""
				+ ",Said,Cat,Hat.,\"A,lot,of,tricks.,I,will,show,them,you."
				+ ",Your,mother,Will,mind,at,all,I,do.\",,Then,Sally,I,Did"
				+ ",what,say.,Our,mother,was,out,of,For,day.,,But,our,said,"
				+ ",\"No!,No!,Make,that,away!,Tell,that,Cat,Hat,You,do,NOT,"
				+ "want,play.,He,should,here.,He,should,about.,He,should,When,your,mother,out!\","
				+ ",\"Now!,Now!,Have,no,fear.,Have,no,fear!\",cat.,\"My,bad,\",Said,Cat,Hat.,\"Why,"
				+ ",we,have,Lots,of,fun,,wish,,that,I,call,UP-UP-UP,fish!\","
				+ ",\"Put,down!\",fish.,\"This,no,fun,at,all!,Put,down!\","
				+ "fish.,\"I,do,NOT,fall!\",,\"Have,no,fear!\",cat.,\"I,"
				+ "will,let,fall.,I,will,hold,up,high,As,I,stand,ball."
				+ ",With,hand!,And,cup,my,hat!,But,that,ALL,I,do!\","
				+ "Said,cat...,,\"Look,at,me!,Look,at,now!\",cat.,\""
				+ "With,cup,On,top,of,my,hat!,I,hold,up,TWO,books!,I,"
				+ "hold,up,fish!,And,litte,toy,ship!,And,some,milk,dish!,"
				+ "And,look!,I,hop,up,down,ball!,But,that,all!,Oh,,no.,"
				+ "That,all...,,\"Look,at,me!,Look,at,me!,Look,at,NOW!,It,"
				+ "fun,have,fun,But,have,how.,I,hold,up,cup,And,milk,cake!,"
				+ "I,hold,up,these,books!,And,rake!,I,hold,toy,And,little,"
				+ "toy,man!,And,look!,With,my,I,hold,red,fan!,I,fan,fan,As,"
				+ "I,hop,ball!,But,that,all.,Oh,,no.,That,all....\",,That,"
				+ "what,said...,Then,he,fell,his,head!,He,came,down,bump,"
				+ "From,up,ball.,And,Sally,I,,We,saw,ALL,things,fall!,,And,"
				+ "our,came,down,,too.,He,fell,into,pot!,He,said,,\"Do,I,"
				+ "this?\",Oh,,no!,I,do,not.,This,game,\",Said,our,as,he,"
				+ "lit.,\"No,,I,do,it,,Not,little,bit!\",,\"Now,look,what,"
				+ "did!\",Said,cat.,\"Now,look,at,this,house!,Look,at,this!,"
				+ "Look,at,that!,You,sank,our,toy,ship,,Sank,it,cake.,You,"
				+ "shook,up,our,And,bent,our,new,rake.,You,SHOULD,NOT,When,"
				+ "our,mother,not.,You,get,out,of,this,house!\",Said,pot.,"
				+ ",\"But,I,here.,Oh,,I,it,lot!\",Said,Cat,Hat,To,pot.,\"I,"
				+ "will,NOT,away.,I,do,NOT,go!,And,so,\",Cat,Hat,,,\"So,so,"
				+ "so...,,I,will,show,Another,that,I,know!\",And,then,he,ran"
				+ ",out.,And,,then,,as,fox,,The,Cat,Hat,Came,box.,A,big,red,"
				+ "wood,box.,It,was,shut,hook.,\"Now,look,at,this,trick,\","
				+ "Said,cat.,\"Take,look!\",,Then,he,got,up,top,With,tip,of,"
				+ "his,hat.,\"I,call,this,FUN-IN-A-BOX,\",Said,cat.,\"In,"
				+ "this,box,things,I,will,show,now.,You,will,these,things,"
				+ "\",Said,bow.,,\"I,will,pick,up,hook.,You,will,see,"
				+ "something,new.,Two,things.,And,I,call,them,Thing,One,"
				+ "Thing,Two.,These,Things,will,bite,you.,They,want,have,"
				+ "fun.\",Then,,out,of,box,Came,Thing,Two,Thing,One!,And,"
				+ "they,ran,us,fast.,They,said,,\"How,do,do?,Would,hands,"
				+ "With,Thing,One,Thing,Two?\",,And,Sally,I,Did,what,do.,"
				+ "So,we,hands,With,Thing,One,Thing,Two.,We,shook,their,"
				+ "hands.,But,our,said,,\"No!,No!,Those,Things,should,In,"
				+ "this,house!,Make,them,go!,\"They,should,When,your,"
				+ "mother,not!,Put,them,out!,Put,them,out!\",Said,pot.,,\""
				+ "Have,no,fear,,little,fish,\",Said,Cat,Hat.,\"These,"
				+ "Things,Things.\",And,he,gave,them,pat.,\"They,tame.,Oh,"
				+ ",so,tame!,They,have,come,play.,They,will,give,some,fun,"
				+ "On,this,wet,,wet,,wet,day.\",,\"Now,,that,they,like,\","
				+ "Said,cat.,\"They,fly,kites,\",Said,Cat,Hat,,\"No!,Not,"
				+ "house!\",Said,pot.,\"They,should,fly,kites,In,house!,"
				+ "They,should,not.,Oh,,things,they,will,bump!,Oh,,things,"
				+ "they,will,hit!,Oh,,I,do,it!,Not,little,bit!\",Then,Sally,"
				+ "I,Saw,them,run,down,hall.,We,saw,those,Things,Bump,their,"
				+ "kites,wall!,Bump!,Thump!,Thump!,Bump!,Down,wall,hall.,"
				+ ",Thing,Two,Thing,One!,They,ran,up!,They,ran,down!,On,"
				+ "string,of,We,saw,Mother's,new,gown!,Her,dots,That,pink,"
				+ ",white,red.,Then,we,saw,bump,On,of,her,bed!,,Then,those,"
				+ "Things,ran,With,big,bumps,,jumps,And,hops,big,thumps,"
				+ "And,all,of,tricks.,And,I,said,,\"I,do,NOT,way,that,they"
				+ ",play,If,Mother,could,see,this,,Oh,,what,would,she,say!"
				+ "\",,Then,our,said,,\"Look!,Look!\",And,our,shook,fear."
				+ ",\"Your,mother,her,way,home!,Do,hear?,Oh,,what,will,she"
				+ ",do,us?,What,will,she,say?,Oh,,she,will,it,To,find,us,"
				+ "this,way!\",,\"So,,DO,something!,Fast!\",fish.,\"Do,"
				+ "hear!,I,saw,her.,Your,mother!,Your,mother,near!,So,"
				+ ",as,as,can,,Think,of,something,do!,You,will,have,get,"
				+ "rid,of,Thing,One,Thing,Two!\",,So,,as,as,I,could,,I,"
				+ "went,after,my,net.,And,I,said,,\"With,my,net,I,get,them"
				+ ",I,bet.,I,bet,,my,net,,I,get,those,Things,yet!\",,Then,"
				+ "I,let,down,my,net.,It,came,down,PLOP!,And,I,them!,At,"
				+ "last!,Thoe,Things,stop.,Then,I,cat,,\"Now,do,as,I,say.,"
				+ "You,pack,up,those,Things,And,take,them,away!\",,\"Oh,"
				+ "dear!\",cat,,\"You,our,game...,Oh,dear.,,What,shame!,"
				+ "What,shame!,What,shame!\",,Then,he,shut,up,Things,In,"
				+ "box,hook.,And,went,away,With,of,look.,,\"That,good,\","
				+ "fish.,\"He,has,gone,away.,Yes.,But,your,mother,will,come."
				+ ",She,will,find,this,big,mess!,And,this,mess,so,big,And,"
				+ "so,so,tall,,We,ca,pick,it,up.,There,no,way,at,all!\","
				+ ",And,THEN!,Who,was,house?,Why,,cat!,\""
				+ "Have,no,fear,of,this,mess,\",Said,Cat,Hat.,\""
				+ "I,always,pick,up,all,my,playthings,And,so...,I,will"
				+ ",show,another,Good,that,I,know!\",,Then,we,saw,him,"
				+ "pick,up,All,things,that,were,down.,He,picked,up,cake,"
				+ ",And,rake,,gown,,And,milk,,strings,,And,books,,dish,"
				+ ",And,fan,,cup,,And,ship,,fish.,And,he,them,away.,"
				+ "Then,he,said,,\"That,that.\",And,then,he,was,gone,With,"
				+ "tip,of,his,hat.,,Then,our,mother,came,And,she,us,two,,\""
				+ "Did,have,any,fun?,Tell,me.,What,do?\",,And,Sally,I,What,"
				+ "say.,Should,we,tell,her,The,things,that,went,that,day?,"
				+ ",Should,we,tell,her,it?,Now,,what,SHOULD,we,do?,Well..."
				+ ",What,would,YOU,do,If,your,mother,asked,YOU?,Fox,Socks,"
				+ "by,Dr.,Seuss]", result.toString());
	}

	@Test
	public void testInput4()
	{
		SpellChecker sc = new BSTSpellChecker();
		sc.loadValidWords("data/a07-valid-words1.txt");
		List<String> result = sc.misspelledWords("data/test2.txt");
		assertEquals("[sup,bro,ya,wanna,go2,thee,partie,l8er,2night?]", result.toString());
	}
	
	@Test
	public void testInput5 ()
	{
		SpellChecker sc = new BSTSpellChecker();
		sc.loadValidWords("data/a07-valid-words1.txt");
		List<String> result = sc.misspelledWords("data/test3.txt");
		assertEquals(result.toString(), "[This,TEST,for,seven.,Without,this,test,,it,will,bE,hard,score,high.]");
		
	}

}
