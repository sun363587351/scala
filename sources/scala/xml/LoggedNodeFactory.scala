package scala.xml;

/** This class logs what the nodefactory is actually doing.
If you want to see what happens during loading, use it like this:
object testLogged with Application {

  val x = new scala.xml.nobinding.NoBindingFactoryAdapter
            with scala.xml.LoggedNodeFactory[scala.xml.Elem]()
            with scala.util.logging.ConsoleLogger;

  Console.println("Start");

  val doc = x.loadXML(new org.xml.sax.InputSource("http://lamp.epfl.ch/~buraq"));

  Console.println("End");

  Console.println(doc);
}

*/
abstract class LoggedNodeFactory[A <: Node]
extends NodeFactory[A]
with scala.util.logging.Logged {

  // configuration values;
  val logNode      = true;
  val logText      = false;
  val logComment   = false;
  val logProcInstr = false;
  val logCharData  = false;

  final val NONE  = 0;
  final val CACHE = 1;
  final val FULL  = 2;
  /** 0 = no loggging, 1 = cache hits, 2 = detail */
  val logCompressLevel  = 1;

  // methods of NodeFactory

  /** logged version of makeNode method */
  override def makeNode(uname: UName, attrSeq:AttributeSeq, children:Seq[Node]): A = {
    if(logNode)
      log("[makeNode for "+uname+"]");

    val hash    = Utility.hashCode(uname, attrSeq.hashCode(), children) ;

    if(logCompressLevel >= FULL) {
      log("[hashcode total:"+hash);
      log(" elem name "+uname+" hash "+(41 * uname.uri.hashCode() % 7 + uname.label.hashCode()));
      log(" attrs     "+attrSeq+" hash "+attrSeq.hashCode());
      log(" children :"+children+" hash "+children.hashCode());
    }

    if(!cache.get( hash ).isEmpty && (logCompressLevel >= CACHE))
      log("[cache hit !]");

    super.makeNode(uname, attrSeq, children);
  }

  override def makeText(s: String) = {
    if(logText)
      log("[makeText:\""+s+"\"]");
    super.makeText( s );
  }

  override def makeComment(s: String): Seq[Comment] = {
    if(logComment)
      log("[makeComment:\""+s+"\"]");
    super.makeComment( s );
  }

  override def makeProcInstr(t: String, s: String): Seq[ProcInstr] = {
    if(logProcInstr)
      log("[makeProcInstr:\""+t+" "+ s+"\"]");
    super.makeProcInstr(t,s);
  }

  /* 2 be removed
  override def makeCharData(s: String) = {}
  */

}
