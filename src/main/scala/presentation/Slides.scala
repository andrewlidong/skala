package presentation

import scalatags.Text.all.*
import SlideHelpers.*

object Slides:

  def all: Seq[Frag] = Seq(
    introSection,
    typeSystemSection,
    oopSection,
    fpSection,
    adtSection,
    implicitsSection,
    advancedTypesSection,
    effectsSection,
    metaprogrammingSection,
    concurrencySection,
    collectionsSection,
    ecosystemSection
  )

  // ---------------------------------------------------------------------------
  // Section 1: Introduction & History
  // ---------------------------------------------------------------------------
  def introSection: Frag = verticalStack(
    titleSlide("Scala: A Deep Dive", "From Type System to Ecosystem"),

    bulletSlide("What is Scala?",
      frag("Created by ", b("Martin Odersky"), " at EPFL, Lausanne (2003)"),
      frag("Name = ", b("Sca"), "lable ", b("La"), "nguage"),
      frag("Runs on JVM, JavaScript (Scala.js), and Native"),
      frag("Influenced by Java, ML, Haskell, Erlang, Smalltalk"),
      frag("Unifies object-oriented and functional programming")
    ),

    bulletSlide("Scala Timeline",
      frag(b("2003"), " — First release at EPFL"),
      frag(b("2006"), " — Scala 2.0: improved type system"),
      frag(b("2013"), " — Scala 2.10: macros, string interpolation, value classes"),
      frag(b("2016"), " — Scala 2.12: Java 8 lambda interop"),
      frag(b("2019"), " — Scala 2.13: new collections library"),
      frag(b("2021"), " — Scala 3.0: DOT calculus, new syntax, enums, given/using"),
      frag(b("2023+"), " — Scala 3.3 LTS: stable long-term support")
    ),

    bulletSlide("Design Philosophy",
      frag("Every value is an object, every operation is a method call"),
      frag("Static typing with powerful type inference"),
      frag("Expression-oriented: everything returns a value"),
      frag("Concise syntax — less boilerplate than Java"),
      frag("Seamless Java interop — use any Java library")
    ),

    codeSlide("Hello, Scala 3!",
      """// Scala 3 top-level definition with @main
@main def hello(): Unit =
  println("Hello, Scala 3!")

// Classic object-oriented entry point
object Main extends App:
  println("Hello from App trait!")

// Expression-oriented: if/else returns a value
val greeting =
  if System.getenv("USER") != null
  then s"Hello, ${System.getenv("USER")}!"
  else "Hello, World!"
"""
    )
  )

  // ---------------------------------------------------------------------------
  // Section 2: Type System Foundations
  // ---------------------------------------------------------------------------
  def typeSystemSection: Frag = verticalStack(
    titleSlide("Type System Foundations", "Scala's Unified Type Hierarchy"),

    slide("Unified Type Hierarchy",
      pre(
        tag("code")(
          cls := "language-plaintext",
          attr("data-trim") := "",
          """                    Any
                   /     \
              AnyVal     AnyRef (java.lang.Object)
             /  |  \      /    |    \
          Int Double ...  String  List  YourClass
              Boolean           \    |    /
                              ... all refs ...
                                    |
                                  Null
                                    |
                                 Nothing"""
        )
      ),
      ul(
        li(cls := "fragment", frag(ic("Any"), " — top type, supertype of everything")),
        li(cls := "fragment", frag(ic("AnyVal"), " — value types (Int, Double, Boolean, ...)")),
        li(cls := "fragment", frag(ic("AnyRef"), " — reference types (alias for java.lang.Object)")),
        li(cls := "fragment", frag(ic("Nothing"), " — bottom type, subtype of everything")),
        li(cls := "fragment", frag(ic("Null"), " — subtype of all AnyRef (deprecated in Scala 3)"))
      )
    ),

    codeSlide("Type Inference",
      """val x = 42              // Int inferred
val s = "hello"         // String inferred
val xs = List(1, 2, 3)  // List[Int] inferred

def add(a: Int, b: Int) = a + b  // Return type Int inferred

// When inference fails or recursion is used, annotate:
def factorial(n: Int): BigInt =
  if n <= 1 then BigInt(1)
  else n * factorial(n - 1)

// Local type inference propagates through chains:
val result = List(1, 2, 3)
  .filter(_ > 1)      // List[Int]
  .map(_.toString)     // List[String]
  .mkString(", ")      // String
"""
    ),

    codeSlide("Literal Types (Scala 3)",
      """val x: 42 = 42           // x has type 42, not Int
val s: "hello" = "hello" // s has type "hello", not String

type Zero = 0
type One  = 1

// Useful for type-level programming
def acceptOnly42(x: 42): Unit = println(s"Got $x")
acceptOnly42(42)  // compiles
// acceptOnly42(43)  // ERROR: Found (43 : Int), Required: (42 : Int)

// Singleton types for compile-time constraints
inline def constString: "fixed" = "fixed"
"""
    ),

    codeSlide("Union & Intersection Types (Scala 3)",
      """// Union types: A | B — a value is either A or B
def handle(input: String | Int): String = input match
  case s: String => s"String: $s"
  case i: Int    => s"Int: $i"

handle("hello")  // "String: hello"
handle(42)       // "Int: 42"

// Intersection types: A & B — a value is both A and B
trait Resettable:
  def reset(): Unit

trait Growable[T]:
  def add(t: T): Unit

def f(x: Resettable & Growable[String]): Unit =
  x.reset()
  x.add("hello")

// Union replaces overloading; Intersection replaces compound types
"""
    ),

    codeSlide("Opaque Types (Scala 3)",
      """// Zero-cost type abstractions — no boxing at runtime
object Types:
  opaque type UserId = Long
  opaque type Email  = String

  object UserId:
    def apply(id: Long): UserId = id

  object Email:
    def apply(e: String): Email = e

  extension (uid: UserId)
    def value: Long = uid

  extension (email: Email)
    def domain: String = email.split("@").last

import Types.*
val id    = UserId(42L)
val email = Email("user@example.com")
email.domain  // "example.com"
// id + 1     // ERROR: UserId is not Long outside Types
"""
    )
  )

  // ---------------------------------------------------------------------------
  // Section 3: Object-Oriented Programming
  // ---------------------------------------------------------------------------
  def oopSection: Frag = verticalStack(
    titleSlide("Object-Oriented Scala", "Classes, Traits, and Mixins"),

    codeSlide("Classes & Companion Objects",
      """// Primary constructor in the class signature
class Person(val name: String, val age: Int):
  def greet: String = s"Hi, I'm $name"
  override def toString: String = s"Person($name, $age)"

// Companion object: same name, same file
// Has access to private members of the class
object Person:
  def apply(name: String): Person = Person(name, 0)
  def fromString(s: String): Option[Person] =
    s.split(",") match
      case Array(name, age) => Some(Person(name.trim, age.trim.toInt))
      case _                => None

val p = Person("Alice")        // uses companion apply
val q = Person.fromString("Bob, 30")  // Option[Person]
"""
    ),

    codeSlide("Traits & Mixins",
      """trait Logging:
  def log(msg: String): Unit =
    println(s"[LOG] $msg")

trait Auditing:
  def audit(action: String): Unit =
    println(s"[AUDIT] $action at ${java.time.Instant.now}")

trait Retrying:
  def retry[T](n: Int)(f: => T): T =
    try f
    catch case e: Exception =>
      if n > 1 then retry(n - 1)(f)
      else throw e

// Mixin composition: combine behaviors
class Service extends Logging, Auditing, Retrying:
  def execute(): Unit =
    log("Starting service")
    audit("execute")
    retry(3) { /* risky operation */ }
"""
    ),

    codeSlide("Enums & Sealed Traits (Scala 3)",
      """// Simple enum
enum Color:
  case Red, Green, Blue

// Parameterized enum (replaces sealed trait hierarchies)
enum Shape:
  case Circle(radius: Double)
  case Rectangle(width: Double, height: Double)
  case Triangle(base: Double, height: Double)

def area(s: Shape): Double = s match
  case Shape.Circle(r)         => math.Pi * r * r
  case Shape.Rectangle(w, h)   => w * h
  case Shape.Triangle(b, h)    => 0.5 * b * h
  // Compiler warns if a case is missing!

// Enums can have methods and implement traits
enum Planet(val mass: Double, val radius: Double):
  case Mercury extends Planet(3.303e+23, 2.4397e6)
  case Venus   extends Planet(4.869e+24, 6.0518e6)
  case Earth   extends Planet(5.976e+24, 6.37814e6)

  def surfaceGravity: Double = 6.67300e-11 * mass / (radius * radius)
"""
    ),

    codeSlide("Case Classes",
      """case class Point(x: Double, y: Double)

val p1 = Point(1.0, 2.0)
val p2 = p1.copy(x = 3.0)      // Point(3.0, 2.0)
val Point(x, y) = p1            // destructure: x=1.0, y=2.0

// Structural equality (not reference equality)
println(p1 == Point(1.0, 2.0))  // true
println(p1.hashCode)            // consistent with equals

// What the compiler generates for you:
// - apply (factory method)     - unapply (extractor)
// - copy (with named args)     - equals & hashCode
// - toString                   - Serializable
// - Product trait              - all fields are val by default
"""
    ),

    codeSlide("Trait Linearization",
      """// Scala resolves diamond inheritance via C3 linearization
trait A:
  def msg: String = "A"

trait B extends A:
  override def msg: String = "B -> " + super.msg

trait C extends A:
  override def msg: String = "C -> " + super.msg

class D extends B, C

val d = new D
println(d.msg)  // "C -> B -> A"

// Linearization order for D:
// D -> C -> B -> A -> AnyRef -> Any
// super calls follow this chain right-to-left
// This is how Scala solves the diamond problem!
"""
    )
  )

  // ---------------------------------------------------------------------------
  // Section 4: Functional Programming
  // ---------------------------------------------------------------------------
  def fpSection: Frag = verticalStack(
    titleSlide("Functional Programming", "Functions as First-Class Citizens"),

    codeSlide("First-Class Functions",
      """// Functions are values, assigned to variables
val double: Int => Int = x => x * 2
val add: (Int, Int) => Int = _ + _

// Underscore shorthand
val nums = List(1, 2, 3, 4, 5)
nums.map(double)         // List(2, 4, 6, 8, 10)
nums.filter(_ % 2 == 0)  // List(2, 4)
nums.reduce(_ + _)       // 15

// Function types are syntactic sugar:
// Int => Int         is  Function1[Int, Int]
// (Int, Int) => Int  is  Function2[Int, Int, Int]

// Functions are objects with an apply method:
val f = new Function1[Int, Int]:
  def apply(x: Int): Int = x * 2
f(5)  // 10
"""
    ),

    codeSlide("Higher-Order Functions",
      """// Functions that take or return functions
def applyTwice[A](f: A => A, x: A): A = f(f(x))

applyTwice(_ + 1, 5)           // 7
applyTwice(_ * 2, 3)           // 12
applyTwice(_.reverse, "abc")   // "abc" (reversed twice)

// Functions that return functions
def multiplier(factor: Int): Int => Int =
  (x: Int) => x * factor

val triple = multiplier(3)
triple(7)  // 21

// compose and andThen
val inc: Int => Int = _ + 1
val dbl: Int => Int = _ * 2

val incThenDbl = inc andThen dbl  // first inc, then dbl
val dblThenInc = inc compose dbl  // first dbl, then inc

incThenDbl(3)  // 8  (3+1=4, 4*2=8)
dblThenInc(3)  // 7  (3*2=6, 6+1=7)
"""
    ),

    codeSlide("Currying & Partial Application",
      """// Curried function: multiple parameter lists
def add(x: Int)(y: Int): Int = x + y
val add5: Int => Int = add(5)   // partially applied
add5(3)                          // 8

// Partial application with placeholder
def log(level: String, msg: String): Unit =
  println(s"[$level] $msg")

val warn: String => Unit = log("WARN", _)
warn("low memory")   // [WARN] low memory

// Converting between curried and uncurried
val f = (a: Int, b: Int, c: Int) => a + b + c
val curried   = f.curried          // Int => Int => Int => Int
val uncurried = curried.uncurried  // (Int, Int, Int) => Int

curried(1)(2)(3)   // 6
uncurried(1, 2, 3) // 6

// Multiple parameter lists enable type inference
def transform[A, B](xs: List[A])(f: A => B): List[B] = xs.map(f)
transform(List(1, 2, 3))(_ * 2)  // A inferred as Int from first list
"""
    ),

    codeSlide("Closures & Purity",
      """// A closure captures variables from its enclosing scope
var counter = 0
val increment: () => Int = () => { counter += 1; counter }
increment()  // 1
increment()  // 2
// increment closes over 'counter' — NOT referentially transparent

// Pure function: same input always gives same output
val pureIncrement: Int => Int = n => n + 1
// No side effects, no external state — referentially transparent

// Why purity matters:
// - Safe to parallelize
// - Easy to test
// - Easy to reason about (substitution model)
// - Memoizable

// Scala doesn't enforce purity, but the idiom is:
// Pure core + impure shell (functional core, imperative shell)
"""
    ),

    codeSlide("Tail Recursion",
      """import scala.annotation.tailrec

// Naive recursion: blows the stack for large n
def factorialUnsafe(n: BigInt): BigInt =
  if n <= 1 then BigInt(1)
  else n * factorialUnsafe(n - 1)

// Tail-recursive version: constant stack space
def factorial(n: BigInt): BigInt =
  @tailrec
  def loop(acc: BigInt, remaining: BigInt): BigInt =
    if remaining <= 1 then acc
    else loop(acc * remaining, remaining - 1)
  loop(1, n)

factorial(10000)  // No stack overflow!

// @tailrec: compiler ERROR if the call is not in tail position
// This is a safety net — guarantees the optimization happens
// Without it, you might silently get a non-tail-recursive function
"""
    )
  )

  // ---------------------------------------------------------------------------
  // Section 5: ADTs & Pattern Matching
  // ---------------------------------------------------------------------------
  def adtSection: Frag = verticalStack(
    titleSlide("Algebraic Data Types", "& Pattern Matching"),

    codeSlide("Sum Types & Product Types",
      """// Product type: A AND B (case class = named product)
case class User(name: String, age: Int)  // String AND Int

// Sum type: A OR B (enum = named sum)
enum Json:
  case JNull
  case JBool(value: Boolean)
  case JNum(value: Double)
  case JStr(value: String)
  case JArr(items: List[Json])
  case JObj(fields: Map[String, Json])

// ADT = Algebraic Data Type = sum of products
// This is the fundamental building block of data modeling in FP
// |Json| = 1 + 2 + |Double| + |String| + |List[Json]| + |Map|
"""
    ),

    codeSlide("Exhaustive Pattern Matching",
      """import Json.*

def render(json: Json): String = json match
  case JNull        => "null"
  case JBool(b)     => b.toString
  case JNum(n)      =>
    if n == n.toLong then n.toLong.toString else n.toString
  case JStr(s)      => s"\"$s\""
  case JArr(items)  =>
    items.map(render).mkString("[", ", ", "]")
  case JObj(fields) =>
    fields
      .map((k, v) => s"\"$k\": ${render(v)}")
      .mkString("{", ", ", "}")

// Compiler WARNS if a case is missing — exhaustivity checking!
// This is why sealed (enum) types are so powerful:
// Add a new variant => compiler tells you every match to update
"""
    ),

    codeSlide("Advanced Pattern Matching",
      """// Nested patterns
val xs = List(1, 2, 3, 4, 5)
xs match
  case Nil                => "empty"
  case head :: Nil        => s"single: $head"
  case head :: _ :: tail  => s"at least 2, head=$head"

// Guards
def classify(x: Int): String = x match
  case n if n < 0   => "negative"
  case 0            => "zero"
  case n if n <= 10 => "small positive"
  case _            => "large positive"

// Tuple patterns
(1, "hello", true) match
  case (1, s, _)  => s"matched: $s"
  case _          => "no match"

// Variable binding with @
List(1, 2, 3) match
  case all @ (_ :: _ :: _) => s"at least 2 elements: $all"
  case _                   => "less than 2"
"""
    ),

    codeSlide("Custom Extractors",
      """// unapply: enables custom pattern matching
object Email:
  def unapply(s: String): Option[(String, String)] =
    s.split("@") match
      case Array(user, domain) if domain.contains(".") =>
        Some((user, domain))
      case _ => None

"user@example.com" match
  case Email(user, domain) =>
    println(s"User: $user, Domain: $domain")
  case other =>
    println(s"Not an email: $other")

// Boolean extractor (no value extracted, just a test)
object Even:
  def unapply(n: Int): Boolean = n % 2 == 0

42 match
  case Even() => "even"   // matches!
  case _      => "odd"

// Extractors decouple pattern matching from data representation
"""
    )
  )

  // ---------------------------------------------------------------------------
  // Section 6: Given/Using & Type Classes
  // ---------------------------------------------------------------------------
  def implicitsSection: Frag = verticalStack(
    titleSlide("Contextual Abstractions", "Given, Using & Type Classes"),

    codeSlide("Given Instances & Using Clauses",
      """// Scala 3 replaces implicits with given/using
trait Ordering[T]:
  def compare(a: T, b: T): Int

given Ordering[Int] with
  def compare(a: Int, b: Int): Int = a - b

given Ordering[String] with
  def compare(a: String, b: String): Int = a.compareTo(b)

def max[T](a: T, b: T)(using ord: Ordering[T]): T =
  if ord.compare(a, b) >= 0 then a else b

max(3, 7)         // 7 — given Ordering[Int] resolved automatically
max("cat", "dog") // "dog"

// Summon a given explicitly
val intOrd = summon[Ordering[Int]]
"""
    ),

    codeSlide("The Type Class Pattern",
      """// Step 1: Define the type class (trait with type parameter)
trait Show[A]:
  extension (a: A) def show: String

// Step 2: Provide instances for specific types
given Show[Int] with
  extension (i: Int) def show: String = i.toString

given Show[String] with
  extension (s: String) def show: String = s"\"$s\""

// Step 3: Derive instances from existing ones
given [A: Show]: Show[List[A]] with
  extension (xs: List[A]) def show: String =
    xs.map(_.show).mkString("[", ", ", "]")

// Step 4: Use it
List(1, 2, 3).show         // "[1, 2, 3]"
List("a", "b").show         // "[\"a\", \"b\"]"

// Type classes = ad-hoc polymorphism
// Add new behaviors to existing types without modifying them
"""
    ),

    codeSlide("Extension Methods",
      """// Add methods to existing types (replacement for implicit classes)
extension (s: String)
  def words: List[String] = s.split("\\s+").toList
  def wordCount: Int = words.length
  def isPalindrome: Boolean = s == s.reverse

"hello world".words        // List("hello", "world")
"hello world".wordCount    // 2
"racecar".isPalindrome     // true

// Generic extensions
extension [A](xs: List[A])
  def second: Option[A] = xs.drop(1).headOption
  def penultimate: Option[A] = xs.dropRight(1).lastOption
  def interleave(ys: List[A]): List[A] =
    xs.zip(ys).flatMap((a, b) => List(a, b))

List(1, 2, 3).second              // Some(2)
List(1, 3, 5).interleave(List(2, 4, 6))  // List(1, 2, 3, 4, 5, 6)
"""
    ),

    codeSlide("Context Bounds & Derives",
      """// Context bound: syntactic sugar for using clauses
// These two are equivalent:
def sort1[T](xs: List[T])(using Ordering[T]): List[T] = xs.sorted
def sort2[T: Ordering](xs: List[T]): List[T] = xs.sorted

// Multiple context bounds
def process[T: Show: Ordering](xs: List[T]): String =
  xs.sorted.map(_.show).mkString(", ")

// derives: automatic type class derivation (Scala 3)
enum Color derives CanEqual:
  case Red, Green, Blue

// Custom derivation with Mirror
case class Config(host: String, port: Int) derives CanEqual

// Implicit conversions (use sparingly!)
import scala.language.implicitConversions

given Conversion[String, Int] with
  def apply(s: String): Int = s.toInt

val n: Int = "42"  // implicit conversion applied
"""
    )
  )

  // ---------------------------------------------------------------------------
  // Section 7: Advanced Type System
  // ---------------------------------------------------------------------------
  def advancedTypesSection: Frag = verticalStack(
    titleSlide("Advanced Type System", "Variance, Bounds & Higher-Kinded Types"),

    codeSlide("Variance",
      """// Covariant (+): if A <: B then F[A] <: F[B]
class Box[+A](val value: A)
val box: Box[Any] = Box[String]("hello")  // OK: String <: Any

// Contravariant (-): if A <: B then F[B] <: F[A]
trait Printer[-A]:
  def print(a: A): Unit

val anyPrinter: Printer[Any] = new Printer[Any]:
  def print(a: Any): Unit = println(a)

val strPrinter: Printer[String] = anyPrinter  // OK: reversed!

// Invariant (default): no subtyping relationship
class MutableBox[A](var value: A)
// val m: MutableBox[Any] = MutableBox[String]("x")  // ERROR

// Rule of thumb:
// Producers are covariant (+)    — List[+A], Option[+A]
// Consumers are contravariant (-) — Function1[-T, +R]
// Mutables are invariant          — Array[A]
"""
    ),

    codeSlide("Type Bounds",
      """// Upper bound: T must be a subtype of Comparable[T]
def maxItem[T <: Comparable[T]](a: T, b: T): T =
  if a.compareTo(b) >= 0 then a else b

// Lower bound: T must be a supertype of B
class Stack[+A]:
  def push[B >: A](item: B): Stack[B] = ???
  // Returns Stack[B] where B >: A — widens the type

// F-bounded polymorphism: recursive type bounds
trait Pet[A <: Pet[A]]:
  def name: String
  def renamed(newName: String): A  // returns same concrete type!

case class Cat(name: String) extends Pet[Cat]:
  def renamed(newName: String): Cat = copy(name = newName)

case class Dog(name: String) extends Pet[Dog]:
  def renamed(newName: String): Dog = copy(name = newName)

val cat: Cat = Cat("Whiskers").renamed("Felix")  // Cat, not Pet
"""
    ),

    codeSlide("Higher-Kinded Types",
      """// F[_] is a type constructor: takes a type, returns a type
// List, Option, Future are all type constructors

trait Functor[F[_]]:
  extension [A](fa: F[A])
    def fmap[B](f: A => B): F[B]

given Functor[List] with
  extension [A](fa: List[A])
    def fmap[B](f: A => B): F[B] = fa.map(f)

given Functor[Option] with
  extension [A](fa: Option[A])
    def fmap[B](f: A => B): F[B] = fa.map(f)

// Now we can write generic code over ANY Functor
def doubleAll[F[_]: Functor](fa: F[Int]): F[Int] =
  fa.fmap(_ * 2)

doubleAll(List(1, 2, 3))   // List(2, 4, 6)
doubleAll(Some(5))          // Some(10)

// HKT = abstraction over type constructors
// This is what enables libraries like Cats and ZIO
"""
    ),

    codeSlide("Path-Dependent Types",
      """// Each instance of a class has its own inner types
class Database:
  class Row(val data: Map[String, String])

  def query(sql: String): List[Row] =
    List(Row(Map("id" -> "1", "name" -> "Alice")))

val db1 = new Database
val db2 = new Database

val rows1: List[db1.Row] = db1.query("SELECT *")
val rows2: List[db2.Row] = db2.query("SELECT *")

// val bad: db2.Row = rows1.head  // ERROR!
// db1.Row and db2.Row are DIFFERENT types!

// This enables the "cake pattern" and type-safe modularity
// Each database instance has its own Row type
// Prevents accidentally mixing rows from different databases
"""
    ),

    codeSlide("Match Types (Scala 3)",
      """// Compute types from other types at compile time
type Elem[X] = X match
  case String       => Char
  case List[t]      => t
  case Array[t]     => t
  case Option[t]    => t

val c: Elem[String]          = 'a'     // Char
val i: Elem[List[Int]]       = 42      // Int
val s: Elem[Option[String]]  = "hi"    // String

// Recursive match types
type Flatten[X] = X match
  case List[List[t]] => Flatten[List[t]]
  case List[t]       => List[t]
  case t             => t

// Match types + inline = compile-time computation
inline def elemType[X]: String = inline erasedValue[X] match
  case _: String    => "Char"
  case _: List[?]   => "element of List"
  case _: Option[?] => "element of Option"
"""
    )
  )

  // ---------------------------------------------------------------------------
  // Section 8: Effects & Monads
  // ---------------------------------------------------------------------------
  def effectsSection: Frag = verticalStack(
    titleSlide("Effects & Monads", "Option, Either, Future & For-Comprehensions"),

    codeSlide("Option: Handling Absence",
      """def parseInt(s: String): Option[Int] =
  s.toIntOption

def divide(a: Int, b: Int): Option[Double] =
  Option.when(b != 0)(a.toDouble / b)

// Chaining with flatMap (monadic bind)
def compute(a: String, b: String): Option[Double] =
  parseInt(a).flatMap(x =>
    parseInt(b).flatMap(y =>
      divide(x, y)))

// Same thing with for-comprehension (syntactic sugar)
def computeFor(a: String, b: String): Option[Double] =
  for
    x      <- parseInt(a)
    y      <- parseInt(b)
    result <- divide(x, y)
  yield result

computeFor("10", "3")  // Some(3.333...)
computeFor("10", "0")  // None
computeFor("abc", "3") // None
"""
    ),

    codeSlide("Either: Typed Errors",
      """enum AppError:
  case NotFound(id: String)
  case InvalidInput(msg: String)
  case Unauthorized

case class User(name: String, age: Int)

def findUser(id: String): Either[AppError, User] =
  if id == "admin" then Right(User("admin", 30))
  else Left(AppError.NotFound(id))

def validateAge(user: User): Either[AppError, User] =
  if user.age >= 18 then Right(user)
  else Left(AppError.InvalidInput("Must be 18+"))

// For-comprehension chains Either (right-biased since Scala 2.12)
val result: Either[AppError, String] =
  for
    user  <- findUser("admin")
    valid <- validateAge(user)
  yield s"Welcome, ${valid.name}"

result  // Right("Welcome, admin")
"""
    ),

    codeSlide("Try & Future",
      """import scala.util.{Try, Success, Failure}
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global

// Try: catch exceptions as values
val parsed: Try[Int] = Try("42".toInt)   // Success(42)
val failed: Try[Int] = Try("abc".toInt)  // Failure(NFE)

parsed.map(_ * 2)           // Success(84)
failed.recover { case _: NumberFormatException => 0 }  // Success(0)

// Future: asynchronous computation
def fetchUser(id: Int): Future[String] = Future {
  Thread.sleep(100)  // simulate I/O
  s"user-$id"
}

// Parallel by default — both start immediately
val f1 = fetchUser(1)
val f2 = fetchUser(2)
val both: Future[(String, String)] = f1.zip(f2)

// Sequence: List[Future[A]] => Future[List[A]]
val all = Future.sequence(List(f1, f2))
"""
    ),

    codeSlide("For-Comprehension Desugaring",
      """// This for-comprehension:
for
  x <- List(1, 2, 3)
  y <- List("a", "b")
  if x > 1
yield (x, y)

// Desugars EXACTLY to:
List(1, 2, 3)
  .flatMap(x =>
    List("a", "b")
      .withFilter(_ => x > 1)
      .map(y => (x, y)))

// Result: List((2,a), (2,b), (3,a), (3,b))

// for-comprehensions work with ANY type that has:
//   map      — for yield
//   flatMap  — for multiple <- generators
//   withFilter — for if guards
//   foreach  — for side-effectful for (no yield)

// This means: Option, Either, Future, List, IO, ZIO, ...
// All use the SAME syntax!
"""
    ),

    codeSlide("The Monad Abstraction",
      """// A Monad is a type constructor with flatMap + pure
// that obeys three laws

trait Monad[F[_]]:
  def pure[A](a: A): F[A]

  extension [A](fa: F[A])
    def flatMap[B](f: A => F[B]): F[B]
    def map[B](f: A => B): F[B] =
      fa.flatMap(a => pure(f(a)))

// The three monad laws:
// 1. Left identity:  pure(a).flatMap(f) == f(a)
// 2. Right identity: fa.flatMap(pure)   == fa
// 3. Associativity:
//    fa.flatMap(f).flatMap(g) ==
//    fa.flatMap(a => f(a).flatMap(g))

// Why care? Monads let you sequence computations
// while abstracting over the effect:
//   Option  = might be absent
//   Either  = might fail with typed error
//   Future  = might be async
//   List    = might have multiple results
//   IO/ZIO  = might do side effects
"""
    )
  )

  // ---------------------------------------------------------------------------
  // Section 9: Metaprogramming
  // ---------------------------------------------------------------------------
  def metaprogrammingSection: Frag = verticalStack(
    titleSlide("Metaprogramming", "Inline, Macros & Compile-Time Computation"),

    codeSlide("Inline: Compile-Time Evaluation",
      """// inline: guaranteed to be inlined at compile time
inline val pi = 3.14159265358979

// inline methods: body is inlined at every call site
inline def assertPositive(x: Int): Int =
  if x <= 0 then
    error("Expected positive number")  // compile-time error!
  else x

val y = assertPositive(5)   // OK, compiles to just 5
// val z = assertPositive(-1) // ERROR at compile time!

// inline if: branches resolved at compile time
inline def debugLog(inline msg: String): Unit =
  inline if scala.compiletime.constValue[false] then
    println(msg)  // dead code — eliminated entirely

// inline match: pattern matching at compile time
inline def typeName[T]: String =
  inline scala.compiletime.erasedValue[T] match
    case _: Int    => "Int"
    case _: String => "String"
    case _         => "Other"
"""
    ),

    codeSlide("Compile-Time Operations",
      """import scala.compiletime.*

// constValue: extract singleton type values at compile time
inline def natToInt[N <: Int]: Int = constValue[N]
val three = natToInt[3]  // 3 — computed at compile time

// constValueTuple: extract tuple of constants
inline def labels[T <: Tuple]: List[String] =
  constValueTuple[T].toList.map(_.toString)

type MyLabels = ("name", "age", "email")
labels[MyLabels]  // List("name", "age", "email")

// summonInline: summon a given instance at compile time
inline def showOf[T]: Show[T] = summonInline[Show[T]]

// summonAll: summon all instances for a tuple of types
inline def showAll[T <: Tuple]: List[Show[?]] =
  summonAll[Tuple.Map[T, Show]].toList.map(_.asInstanceOf[Show[?]])
"""
    ),

    codeSlide("Quotes & Splices (Macros)",
      """import scala.quoted.*

// Macros bridge compile-time and run-time
// '{ expr }  — quote: turn runtime code into a compile-time tree
// ${ expr }  — splice: turn a compile-time tree into runtime code

// A macro that shows the source code of an expression
def inspectImpl(expr: Expr[Any])(using Quotes): Expr[String] =
  val sourceCode = expr.show  // get source representation
  Expr(s"Expression: $sourceCode")

// The user-facing inline method
inline def inspect(inline expr: Any): String =
  ${ inspectImpl('expr) }

// Usage:
inspect(1 + 2)          // "Expression: 1.+(2)"
inspect(List(1).map(x => x + 1))
// "Expression: List.apply(1).map(x => x.+(1))"
"""
    ),

    codeSlide("Mirror-Based Derivation",
      """import scala.deriving.Mirror

// Mirror provides compile-time structural information about types

// For a product type (case class):
case class Person(name: String, age: Int)
// Mirror.ProductOf[Person] provides:
//   MirroredType       = Person
//   MirroredElemTypes   = (String, Int)
//   MirroredElemLabels  = ("name", "age")
//   MirroredLabel       = "Person"

inline def fieldNames[T](using m: Mirror.ProductOf[T]): List[String] =
  constValueTuple[m.MirroredElemLabels].toList.map(_.toString)

fieldNames[Person]  // List("name", "age")

// For a sum type (enum):
// Mirror.SumOf provides MirroredElemTypes for all variants
// This is how libraries like Circe derive JSON codecs automatically
"""
    )
  )

  // ---------------------------------------------------------------------------
  // Section 10: Concurrency
  // ---------------------------------------------------------------------------
  def concurrencySection: Frag = verticalStack(
    titleSlide("Concurrency", "Futures, Actors & Effect Systems"),

    codeSlide("Futures: Async Building Blocks",
      """import scala.concurrent.*
import scala.concurrent.duration.*
import ExecutionContext.Implicits.global

val f1 = Future { Thread.sleep(100); "result1" }
val f2 = Future { Thread.sleep(100); "result2" }

// Both started immediately — true parallelism
val combined: Future[(String, String)] = f1.zip(f2)

// Transform results
val mapped: Future[Int] = f1.map(_.length)

// Sequence: List[Future[A]] => Future[List[A]]
val futures = List(Future(1), Future(2), Future(3))
val all: Future[List[Int]] = Future.sequence(futures)

// Error recovery
val safe = f1.recover { case _: Exception => "fallback" }
val retried = f1.recoverWith { case _ => Future("retry") }

// firstCompletedOf: race multiple futures
val fastest = Future.firstCompletedOf(List(f1, f2))
"""
    ),

    codeSlide("Akka Typed Actors",
      """// Actor model: message-passing concurrency
// Each actor has a mailbox, processes one message at a time
import akka.actor.typed.*
import akka.actor.typed.scaladsl.*

enum Command:
  case Greet(name: String, replyTo: ActorRef[String])
  case Stop

def greeter: Behavior[Command] =
  Behaviors.receive { (context, message) =>
    message match
      case Command.Greet(name, replyTo) =>
        context.log.info(s"Greeting $name")
        replyTo ! s"Hello, $name!"
        Behaviors.same
      case Command.Stop =>
        Behaviors.stopped
  }

// Actors guarantee:
// - No shared mutable state
// - Messages processed sequentially per actor
// - Location transparency (local or remote)
"""
    ),

    codeSlide("ZIO: Typed Functional Effects",
      """import zio.*

// ZIO[R, E, A]:
//   R = environment (dependencies)
//   E = error type
//   A = success type

val program: ZIO[Any, java.io.IOException, Unit] =
  for
    _    <- Console.printLine("What is your name?")
    name <- Console.readLine
    _    <- Console.printLine(s"Hello, $name!")
  yield ()

// Error handling is TYPED — no unchecked exceptions
def divide(a: Int, b: Int): ZIO[Any, ArithmeticException, Int] =
  if b == 0 then ZIO.fail(ArithmeticException("/ by zero"))
  else ZIO.succeed(a / b)

// Fibers: lightweight green threads (millions possible)
val parallel = for
  fiber1 <- longTask1.fork   // starts on a fiber
  fiber2 <- longTask2.fork
  r1     <- fiber1.join      // wait for result
  r2     <- fiber2.join
yield (r1, r2)
"""
    ),

    codeSlide("Cats Effect: Pure FP Runtime",
      """import cats.effect.*

// IO[A]: a description of a side-effectful computation
// Nothing runs until the "end of the world" (IOApp)
val program: IO[Unit] =
  for
    _   <- IO.println("Hello from Cats Effect!")
    now <- IO.realTime
    _   <- IO.println(s"Current time: $now")
  yield ()

// Safe resource management with Resource
def managed: Resource[IO, java.io.InputStream] =
  Resource.make(
    IO(java.io.FileInputStream("file.txt"))
  )(stream => IO(stream.close()))

// Structured concurrency
val parallel: IO[(String, Int)] =
  (fetchName, fetchAge).parTupled  // runs in parallel

// Cancelation, timeouts, error handling — all composable
val withTimeout = program.timeout(5.seconds)
"""
    )
  )

  // ---------------------------------------------------------------------------
  // Section 11: Collections
  // ---------------------------------------------------------------------------
  def collectionsSection: Frag = verticalStack(
    titleSlide("Collections Library", "Immutable, Lazy & Parallel"),

    bulletSlide("Collection Hierarchy",
      frag(ic("Iterable"), " — root of all collections"),
      frag(ic("Seq"), " — ordered: List, Vector, ArraySeq"),
      frag(ic("Set"), " — unique elements: HashSet, TreeSet, BitSet"),
      frag(ic("Map"), " — key-value pairs: HashMap, TreeMap"),
      frag("Immutable by default (", ic("scala.collection.immutable"), ")"),
      frag("Mutable available (", ic("scala.collection.mutable"), ")"),
      frag("All share a common, uniform API")
    ),

    codeSlide("Immutable Collections",
      """val list = List(1, 2, 3)
val prepended = 0 :: list          // List(0, 1, 2, 3)
val appended  = list :+ 4         // List(1, 2, 3, 4)
val concatenated = list ++ List(4, 5)  // List(1, 2, 3, 4, 5)

val vec = Vector(1, 2, 3)
val updated = vec.updated(1, 99)  // Vector(1, 99, 3) — O(log32 n)

val m = Map("a" -> 1, "b" -> 2)
val m2 = m + ("c" -> 3)          // Map(a->1, b->2, c->3)
val m3 = m - "a"                  // Map(b -> 2)
m.get("a")                        // Some(1)
m.get("z")                        // None

val s = Set(1, 2, 3, 2, 1)       // Set(1, 2, 3)
s + 4                             // Set(1, 2, 3, 4)
s & Set(2, 3, 4)                  // Set(2, 3) — intersection
s | Set(4, 5)                     // Set(1, 2, 3, 4, 5) — union
"""
    ),

    codeSlide("Transformations",
      """val words = List("hello", "world", "scala", "is", "great")

words.map(_.toUpperCase)          // List("HELLO", ...)
words.flatMap(_.toList)           // List(h, e, l, l, o, ...)
words.filter(_.length > 4)       // List("hello", "world", "scala", "great")
words.foldLeft(0)(_ + _.length)  // 22 (total characters)
words.groupBy(_.head)            // Map(h -> List(hello), ...)
words.zip(LazyList.from(1))      // List((hello,1), (world,2), ...)
words.sortBy(_.length)           // List("is", "scala", "hello", ...)
words.distinct                    // (already distinct)
words.sliding(2).toList          // List(List(hello,world), ...)

// collect: partial function = filter + map in one step
List(1, "two", 3, "four").collect { case i: Int => i * 2 }
// List(2, 6)

// groupMapReduce: group + map + reduce in one pass
words.groupMapReduce(_.head)(_.length)(_ + _)
// Map(h -> 5, w -> 5, s -> 5, i -> 2, g -> 5)
"""
    ),

    codeSlide("Lazy Collections & Views",
      """// LazyList: elements computed on demand, memoized
val fibs: LazyList[BigInt] =
  BigInt(0) #:: BigInt(1) #::
    fibs.zip(fibs.tail).map((a, b) => a + b)

fibs.take(10).toList
// List(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)

val nats = LazyList.from(1)  // infinite!
nats.filter(_ % 7 == 0).take(5).toList  // List(7, 14, 21, 28, 35)

// Views: lazy intermediate operations (no intermediate collections)
(1 to 1_000_000)
  .view
  .map(_ * 2)
  .filter(_ % 6 == 0)
  .take(5)
  .toList   // List(6, 12, 18, 24, 30)
// Only 5 elements actually computed — no million-element intermediaries!

// Iterator: single-pass, non-reusable lazy traversal
val iter = Iterator.from(1).map(_ * 2).take(5)
iter.toList  // List(2, 4, 6, 8, 10)
"""
    ),

    twoColumnSlide("Performance Characteristics",
      tag("table")(style := "font-size: 0.6em;",
        tag("thead")(tag("tr")(
          tag("th")("Operation"), tag("th")("List"), tag("th")("Vector"), tag("th")("ArraySeq")
        )),
        tag("tbody")(
          tag("tr")(tag("td")("prepend"), tag("td")("O(1)"), tag("td")("O(log n)"), tag("td")("O(n)")),
          tag("tr")(tag("td")("append"), tag("td")("O(n)"), tag("td")("O(log n)"), tag("td")("O(n)")),
          tag("tr")(tag("td")("access(i)"), tag("td")("O(n)"), tag("td")("O(log n)"), tag("td")("O(1)")),
          tag("tr")(tag("td")("update(i)"), tag("td")("O(n)"), tag("td")("O(log n)"), tag("td")("O(n)")),
          tag("tr")(tag("td")("head/tail"), tag("td")("O(1)"), tag("td")("O(log n)"), tag("td")("O(1)/O(n)"))
        )
      ),
      ul(style := "font-size: 0.6em;",
        li(frag(b("List"), ": best for head/tail recursion, pattern matching")),
        li(frag(b("Vector"), ": best general-purpose (balanced performance)")),
        li(frag(b("ArraySeq"), ": best for indexed access")),
        li(frag(b("HashMap"), ": O(1) amortized lookup")),
        li(frag(b("TreeMap"), ": O(log n) sorted operations"))
      )
    )
  )

  // ---------------------------------------------------------------------------
  // Section 12: Ecosystem
  // ---------------------------------------------------------------------------
  def ecosystemSection: Frag = verticalStack(
    titleSlide("The Scala Ecosystem", "Libraries, Frameworks & Tools"),

    bulletSlide("Build Tools",
      frag(b("sbt"), " — the standard: interactive, incremental compilation, plugins ecosystem"),
      frag(b("Mill"), " — simpler config, builds defined in Scala, fast"),
      frag(b("Gradle"), " — Scala plugin available, good for mixed Java/Scala"),
      frag(b("scala-cli"), " — lightweight scripting, perfect for single files and experiments")
    ),

    bulletSlide("Core Libraries",
      frag(b("Cats"), " — functional abstractions (Monad, Functor, Applicative, ...)"),
      frag(b("ZIO"), " — effect system with typed errors, fibers, layers"),
      frag(b("Akka/Pekko"), " — actors, streams, HTTP, clustering"),
      frag(b("http4s"), " — pure FP HTTP server & client"),
      frag(b("Circe"), " — JSON encoding/decoding, automatic derivation"),
      frag(b("Doobie"), " — pure FP JDBC layer"),
      frag(b("fs2"), " — functional streaming I/O"),
      frag(b("Chimney"), " — data transformation between case classes")
    ),

    codeSlide("Apache Spark: Distributed Computing",
      """// Spark: the killer app that put Scala on the map
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.*

val spark = SparkSession.builder()
  .appName("Analysis")
  .master("local[*]")
  .getOrCreate()

import spark.implicits.*

case class Sale(product: String, amount: Double, region: String)

val sales = spark.read.parquet("sales.parquet").as[Sale]

// Type-safe dataset operations
val regionTotals = sales
  .groupBy($"region")
  .agg(
    sum($"amount").as("total"),
    count("*").as("transactions")
  )
  .orderBy($"total".desc)

regionTotals.show()
// Runs distributed across a cluster — same Scala syntax!
"""
    ),

    slide("Scala: Where Types Meet Expressiveness",
      ul(
        li(cls := "fragment", frag(b("Unified OOP + FP"), " — use the right paradigm for each problem")),
        li(cls := "fragment", frag(b("Powerful type system"), " — catch errors at compile time, not runtime")),
        li(cls := "fragment", frag(b("Concise & expressive"), " — say more with less code")),
        li(cls := "fragment", frag(b("Rich ecosystem"), " — from web apps to distributed data processing")),
        li(cls := "fragment", frag(b("Scala 3"), " — cleaner syntax, better tooling, the best version yet"))
      ),
      br,
      p(cls := "fragment", style := "text-align: center; font-size: 1.5em;",
        b("Thank you!")
      )
    )
  )
