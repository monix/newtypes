// When the user clicks on the search box, we want to toggle the search dropdown
function displayToggleSearch(e) {
  e.preventDefault();
  e.stopPropagation();

  closeDropdownSearch(e);
  
  if (idx === null) {
    console.log("Building search index...");
    prepareIdxAndDocMap();
    console.log("Search index built.");
  }
  const dropdown = document.querySelector("#search-dropdown-content");
  if (dropdown) {
    if (!dropdown.classList.contains("show")) {
      dropdown.classList.add("show");
    }
    document.addEventListener("click", closeDropdownSearch);
    document.addEventListener("keydown", searchOnKeyDown);
    document.addEventListener("keyup", searchOnKeyUp);
  }
}

//We want to prepare the index only after clicking the search bar
var idx = null
const docMap = new Map()

function prepareIdxAndDocMap() {
  const docs = [  
    {
      "title": "Change Log",
      "url": "/CHANGELOG.html",
      "content": "Change Log All notable changes to this project will be documented in this file. The format is based on Keep a Changelog and this project adheres to Semantic Versioning. [1.0.0-RC1] - 2020-07-04 Added Foo Changed Bar Removed FooBar Fixed Baz [Unreleased] Initial commit from sbt --sbt-version 1.3.13 new alexandru/typelevel-library.g8"
    } ,    
    {
      "title": "Code of Conduct",
      "url": "/CODE_OF_CONDUCT.html",
      "content": "Code of Conduct We are committed to providing a friendly, safe and welcoming environment for all, regardless of level of experience, gender, gender identity and expression, sexual orientation, disability, personal appearance, body size, race, ethnicity, age, religion, nationality, or other such characteristics. Everyone is expected to follow the Scala Code of Conduct when discussing the project on the available communication channels. If you are being harassed, please contact us immediately so that we can support you. Moderation Any questions, concerns, or moderation requests please contact a maintainer of the project: Alexandru Nedelcu: Website / GitHub / Gitter / Twitter"
    } ,    
    {
      "title": "Contributing",
      "url": "/CONTRIBUTING.html",
      "content": "Contributing to Newtypes The Newtypes project welcomes contributions from anybody wishing to participate. All code or documentation that is provided must be licensed with the same license that Newtypes is licensed with (Apache 2.0, see LICENSE). Code of Conduct People are expected to follow the Scala Code of Conduct when discussing Newtypes on the Github page, Gitter channel, or other venues. We hope that our community will be respectful, helpful, and kind. If you find yourself embroiled in a situation that becomes heated, or that fails to live up to our expectations, you should disengage and contact one of the project maintainers in private. We hope to avoid letting minor aggressions and misunderstandings escalate into larger problems. General Workflow Make sure you can license your work under Apache 2.0 Before starting to work, make sure there is a ticket in the issue or create one first. It can help accelerate the acceptance process if the change is agreed upon If you don’t have write access to the repository, you should do your work in a local branch of your own fork and then submit a pull request. If you do have write access to the repository, never work directly on the main branch. Submit a Pull Request. Anyone can comment on a pull request and you are expected to answer questions or to incorporate feedback. It is not allowed to force push to the branch on which the pull request is based. General Guidelines It is recommended that the work is accompanied by unit tests. The commit messages should be clear and short one lines, if more details are needed, specify a body. New source files should be accompanied by the copyright header (should be taken care of automatically by the build tool, but do check). Follow the structure of the code in this repository and the indentation rules used. Your first commit request should be accompanied with a change to the AUTHORS file, adding yourself to the authors list. License All code must be licensed under the Apache 2.0 license and all files must include the following copyright header: Copyright (c) $today.year the Newtypes contributors. See the project homepage at: https://newtypes.monix.io/ Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License."
    } ,    
    {
      "title": "License",
      "url": "/LICENSE.html",
      "content": "Apache License Version 2.0, January 2004 &lt;http://www.apache.org/licenses/&gt; Terms and Conditions for use, reproduction, and distribution 1. Definitions “License” shall mean the terms and conditions for use, reproduction, and distribution as defined by Sections 1 through 9 of this document. “Licensor” shall mean the copyright owner or entity authorized by the copyright owner that is granting the License. “Legal Entity” shall mean the union of the acting entity and all other entities that control, are controlled by, or are under common control with that entity. For the purposes of this definition, “control” means (i) the power, direct or indirect, to cause the direction or management of such entity, whether by contract or otherwise, or (ii) ownership of fifty percent (50%) or more of the outstanding shares, or (iii) beneficial ownership of such entity. “You” (or “Your”) shall mean an individual or Legal Entity exercising permissions granted by this License. “Source” form shall mean the preferred form for making modifications, including but not limited to software source code, documentation source, and configuration files. “Object” form shall mean any form resulting from mechanical transformation or translation of a Source form, including but not limited to compiled object code, generated documentation, and conversions to other media types. “Work” shall mean the work of authorship, whether in Source or Object form, made available under the License, as indicated by a copyright notice that is included in or attached to the work (an example is provided in the Appendix below). “Derivative Works” shall mean any work, whether in Source or Object form, that is based on (or derived from) the Work and for which the editorial revisions, annotations, elaborations, or other modifications represent, as a whole, an original work of authorship. For the purposes of this License, Derivative Works shall not include works that remain separable from, or merely link (or bind by name) to the interfaces of, the Work and Derivative Works thereof. “Contribution” shall mean any work of authorship, including the original version of the Work and any modifications or additions to that Work or Derivative Works thereof, that is intentionally submitted to Licensor for inclusion in the Work by the copyright owner or by an individual or Legal Entity authorized to submit on behalf of the copyright owner. For the purposes of this definition, “submitted” means any form of electronic, verbal, or written communication sent to the Licensor or its representatives, including but not limited to communication on electronic mailing lists, source code control systems, and issue tracking systems that are managed by, or on behalf of, the Licensor for the purpose of discussing and improving the Work, but excluding communication that is conspicuously marked or otherwise designated in writing by the copyright owner as “Not a Contribution.” “Contributor” shall mean Licensor and any individual or Legal Entity on behalf of whom a Contribution has been received by Licensor and subsequently incorporated within the Work. 2. Grant of Copyright License Subject to the terms and conditions of this License, each Contributor hereby grants to You a perpetual, worldwide, non-exclusive, no-charge, royalty-free, irrevocable copyright license to reproduce, prepare Derivative Works of, publicly display, publicly perform, sublicense, and distribute the Work and such Derivative Works in Source or Object form. 3. Grant of Patent License Subject to the terms and conditions of this License, each Contributor hereby grants to You a perpetual, worldwide, non-exclusive, no-charge, royalty-free, irrevocable (except as stated in this section) patent license to make, have made, use, offer to sell, sell, import, and otherwise transfer the Work, where such license applies only to those patent claims licensable by such Contributor that are necessarily infringed by their Contribution(s) alone or by combination of their Contribution(s) with the Work to which such Contribution(s) was submitted. If You institute patent litigation against any entity (including a cross-claim or counterclaim in a lawsuit) alleging that the Work or a Contribution incorporated within the Work constitutes direct or contributory patent infringement, then any patent licenses granted to You under this License for that Work shall terminate as of the date such litigation is filed. 4. Redistribution You may reproduce and distribute copies of the Work or Derivative Works thereof in any medium, with or without modifications, and in Source or Object form, provided that You meet the following conditions: (a) You must give any other recipients of the Work or Derivative Works a copy of this License; and (b) You must cause any modified files to carry prominent notices stating that You changed the files; and (c) You must retain, in the Source form of any Derivative Works that You distribute, all copyright, patent, trademark, and attribution notices from the Source form of the Work, excluding those notices that do not pertain to any part of the Derivative Works; and (d) If the Work includes a “NOTICE” text file as part of its distribution, then any Derivative Works that You distribute must include a readable copy of the attribution notices contained within such NOTICE file, excluding those notices that do not pertain to any part of the Derivative Works, in at least one of the following places: within a NOTICE text file distributed as part of the Derivative Works; within the Source form or documentation, if provided along with the Derivative Works; or, within a display generated by the Derivative Works, if and wherever such third-party notices normally appear. The contents of the NOTICE file are for informational purposes only and do not modify the License. You may add Your own attribution notices within Derivative Works that You distribute, alongside or as an addendum to the NOTICE text from the Work, provided that such additional attribution notices cannot be construed as modifying the License. You may add Your own copyright statement to Your modifications and may provide additional or different license terms and conditions for use, reproduction, or distribution of Your modifications, or for any such Derivative Works as a whole, provided Your use, reproduction, and distribution of the Work otherwise complies with the conditions stated in this License. 5. Submission of Contributions Unless You explicitly state otherwise, any Contribution intentionally submitted for inclusion in the Work by You to the Licensor shall be under the terms and conditions of this License, without any additional terms or conditions. Notwithstanding the above, nothing herein shall supersede or modify the terms of any separate license agreement you may have executed with Licensor regarding such Contributions. 6. Trademarks This License does not grant permission to use the trade names, trademarks, service marks, or product names of the Licensor, except as required for reasonable and customary use in describing the origin of the Work and reproducing the content of the NOTICE file. 7. Disclaimer of Warranty Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE. You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License. 8. Limitation of Liability In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law (such as deliberate and grossly negligent acts) or agreed to in writing, shall any Contributor be liable to You for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work (including but not limited to damages for loss of goodwill, work stoppage, computer failure or malfunction, or any and all other commercial damages or losses), even if such Contributor has been advised of the possibility of such damages. 9. Accepting Warranty or Additional Liability While redistributing the Work or Derivative Works thereof, You may choose to offer, and charge a fee for, acceptance of support, warranty, indemnity, or other liability obligations and/or rights consistent with this License. However, in accepting such obligations, You may act only on Your own behalf and on Your sole responsibility, not on behalf of any other Contributor, and only if You agree to indemnify, defend, and hold each Contributor harmless for any liability incurred by, or claims asserted against, such Contributor by reason of your accepting any such warranty or additional liability. END OF TERMS AND CONDITIONS APPENDIX: How to apply the Apache License to your work To apply the Apache License to your work, attach the following boilerplate notice, with the fields enclosed by brackets [] replaced with your own identifying information. (Don’t include the brackets!) The text should be enclosed in the appropriate comment syntax for the file format. We also recommend that a file or class name and description of purpose be included on the same “printed page” as the copyright notice for easier identification within third-party archives. Copyright [yyyy] [name of copyright owner] Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License."
    } ,    
    {
      "title": "Integration with Circe (JSON encoding/decoding)",
      "url": "/docs/circe.html",
      "content": "Integration with Circe (JSON encoding/decoding) Importing the Circe integration: // For Circe version 0.14.x libraryDependencies += \"io.monix\" %% \"newtypes-circe-v0.14\" % \"0.2.1\" Usage: import monix.newtypes._ import monix.newtypes.integrations.DerivedCirceCodec type Email = Email.Type object Email extends NewtypeValidated[String] with DerivedCirceCodec { def apply(v: String): Either[BuildFailure[Type], Type] = if (v.contains(\"@\")) Right(unsafeCoerce(v)) else Left(BuildFailure(\"missing @\")) } You can now serialize and deserialize to/from JSON: import io.circe.syntax._ import io.circe.parser.decode decode[Email]( Email.unsafe(\"noreply@alexn.org\") .asJson .noSpaces ) // res0: Either[io.circe.Error, Email] = Right(value = \"noreply@alexn.org\") decode[Email]( Email.unsafe(\"la, la, lalalala\") .asJson .noSpaces ) // res1: Either[io.circe.Error, Email] = Left( // value = DecodingFailure(Invalid Email — missing @, List()) // )"
    } ,    
    {
      "title": "Defining Newtypes",
      "url": "/docs/core.html",
      "content": "Defining Newtypes Importing the dependency: libraryDependencies += \"io.monix\" %% \"newtypes-core\" % \"0.2.1\" Table of contents: Newtype NewtypeWrapped NewtypeValidated Deriving type-class instances NewtypeK and NewtypeCovariantK Newsubtype Encoders and Decoders Newtype Newtype exposes the base encoding for newtypes over types with no type parameters. It provides no pre-defined builders, such that you need to provide apply or unapply by yourself: import monix.newtypes._ type EmailAddress = EmailAddress.Type object EmailAddress extends Newtype[String] { def apply(value: String): Option[EmailAddress] = if (value.contains(\"@\")) Some(unsafeCoerce(value)) else None def unapply[A](a: A)(implicit ev: A =:= Type): Some[String] = Some(value(ev(a))) } It’s more convenient to work with NewtypeWrapped or NewtypeValidated, as shown below. NewtypeWrapped We can use NewtypeWrapped for creating newtypes, as simple wrappers (no validation) over types with no type parameters: import monix.newtypes._ type FirstName = FirstName.Type object FirstName extends NewtypeWrapped[String] // Usage: val fname = FirstName(\"Alex\") // To coerce into its source type again: fname.value //=&gt; val res: String = \"Alex\" Note, this is a type-safe alias, aka an “opaque type”, so our FirstName is not seen as a String or vice-versa: // ERROR — should fail at compile-time val fname1: FirstName = \"Alex\" // ERROR — should fail at compile-time too val fname2: String = FirstName(\"Alex\") Pattern matching is also possible: fname match { case FirstName(str) =&gt; s\"Name: $str\" } Note that due to type-erasure we are restricting the pattern matching that’s possible. This doesn’t work: // ERROR — should fail at compile-time (fname: Any) match { case FirstName(_) =&gt; \"Matches!\" case _ =&gt; \"Nope!\" } This doesn’t work either: // ERROR — should fail at compile-time \"Alex\" match { case FirstName(_) =&gt; \"Matches!\" case _ =&gt; \"Nope!\" } And trying to do a regular isInstanceOf checks should trigger at least a Scala warning, due to the type being erased, hopefully you’re working with -Xfatal-warnings: // ERROR — should fail at compile-time fname match { case ref: FirstName =&gt; \"Matches!\" } NewtypeValidated Use NewtypeValidated for creating newtypes that have extra validation: import monix.newtypes._ type EmailAddress = EmailAddress.Type object EmailAddress extends NewtypeValidated[String] { def apply(v: String): Either[BuildFailure[Type], Type] = if (v.contains(\"@\")) Right(unsafeCoerce(v)) else Left(BuildFailure(\"missing @\")) } We only allow strings with a certain format to be considered valid email addresses: EmailAddress(\"noreply@alexn.org\") match { case Right(address) =&gt; s\"Validated: ${address.value}\" case Left(e) =&gt; s\"Error: $e\" } // res9: String = \"Validated: noreply@alexn.org\" There are cases in which the validation needs to be bypassed, which can be done via the “unsafe” builder: val address = EmailAddress.unsafe(\"noreply@alexn.org\") And, we can pattern match it to extract its value: address match { case EmailAddress(str) =&gt; s\"Matched: $str\" } // res10: String = \"Matched: noreply@alexn.org\" Note the same caveats apply for pattern matching: // ERROR — should fail at compile-time (address: Any) match { case EmailAddress(_) =&gt; () case _ =&gt; () } // ERROR — should fail at compile-time \"noreply@alexn.org\" match { case EmailAddress(_) =&gt; () case _ =&gt; () } // ERROR — triggers at least a warning at compile-time address match { case _: EmailAddress =&gt; () case _ =&gt; () } Deriving type-class instances We can derive type class instances, with a derive helper available in Newtype: import cats._ import cats.implicits._ import monix.newtypes._ type FirstName = FirstName.Type object FirstName extends NewtypeWrapped[String] { implicit val eq: Eq[FirstName] = derive implicit val show: Show[FirstName] = derive } // ... val fname = FirstName(\"Alex\") assert(fname.show == \"Alex\") assert(Eq[FirstName].eqv(fname, FirstName(\"Alex\"))) NewtypeK and NewtypeCovariantK NewtypeK is for defining newtypes over types with an invariant type parameter. NewtypeCovariantK inherits from it and is for defining newtypes over types with a covariant type parameter. import cats._ import cats.implicits._ import monix.newtypes._ type NonEmptyList[A] = NonEmptyList.Type[A] object NonEmptyList extends NewtypeCovariantK[List] { // Builder forces at least one element def apply[A](head: A, tail: A*): NonEmptyList[A] = unsafeCoerce(head :: tail.toList) // Exposes (head, tail) def unapply[F[_], A](list: F[A])( implicit ev: F[A] =:= NonEmptyList[A] ): Some[(A, List[A])] = { val l = value(list) Some((l.head, l.tail)) } // Utilities specific for NonEmptyList implicit final class NelOps[A](val self: NonEmptyList[A]) { def head: A = self.value.head def tail: List[A] = self.value.tail } implicit def eq[A: Eq]: Eq[NonEmptyList[A]] = derive // Deriving type-class instance working on F[_], notice use of deriveK implicit val traverse: Traverse[NonEmptyList] = deriveK // Deriving type-class instance working on F[_], notice use of deriveK implicit val monad: Monad[NonEmptyList] = deriveK } And usage: val colors = NonEmptyList(\"Red\", \"Green\", \"Blue\") // colors: NonEmptyList.Type[String] = List(\"Red\", \"Green\", \"Blue\") colors.head // res18: String = \"Red\" colors.tail // res19: List[String] = List(\"Green\", \"Blue\") // Pattern matching works colors match { case NonEmptyList(head, tail) =&gt; () } // Covariance works val any: NonEmptyList[Any] = colors // any: NonEmptyList[Any] = List(\"Red\", \"Green\", \"Blue\") // It can be traversed NonEmptyList(Option(\"Red\"), Option(\"Green\"), Option(\"Blue\")) .sequence // res21: Option[NonEmptyList.Type[String]] = Some( // value = List(\"Red\", \"Green\", \"Blue\") // ) With NewtypeK and NewtypeCovariantK you have to provide the apply, unapply, and other utilities by yourself. Which makes sense, as these are more complex types to deal with. Newsubtype Newsubtype exposes the base encoding for new-subtypes over types with no type parameters. It functions exactly the same as Newtype, except as a subtype of the underlying type instead of as an entirely new type. It provides the same utility classes as Newtype, including NewsubtypeWrapped, NewsubtypeValidated, NewsubtypeK, and NewsubtypeCovariantK. There are two core benefits of Newsubtype and its variants: Newsubtypes of primitives are unboxed in scala 2 (in scala 3 both should be unboxed as expected). There is reduced boilerplate in dealing with the underlying type. That said, unless you know you need Newsubtype, you’re likely better off living with the extra boilerplate in a production system, as Newsubtype can lead to accidental unwrapping. Newsubtypes don’t need to declare forwarding methods or reimplement any methods on their underlying types: import monix.newtypes._ type Level = Level.Type object Level extends NewsubtypeWrapped[Int] val myLevel: Level = Level(5) // myLevel: Level = 5 Thus, we can do things like call + from Int on our new subtype, however this unwraps our result to Int: val anotherLevel: Int = myLevel + 1 // anotherLevel: Int = 6 The likely desired result type doesn’t work: // ERROR — should fail at compile-time val newLevel: Level = myLevel + 1 We would need to re-wrap our results, which could be prohibitively expensive depending on the validation logic on the Newsubtype: val newLevel: Level = Level(myLevel + 1) // newLevel: Level = 6 Newsubtype can unwrap in more subtle and potentially dangerous ways. As a simple and contrived example, instances of either of Map[Level, Int] or List[Level] have apply methods that can take our subtype Level but would return dramatically different results. If we were using the Map apply and someone else changed the data type to List, our code would continue to compile but silently produce invalid results. If our Level were a Newtype instead, code using the List apply method but expecting the Map apply would now fail at compile time. Encoders and Decoders You can automatically derive encoders based on HasExtractor instances. All newtypes have a HasExtractor instance defined. Here’s how to automatically derive io.circe.Encoder: import monix.newtypes._ import io.circe._ implicit def jsonEncoder[T, S](implicit extractor: HasExtractor.Aux[T, S], enc: Encoder[S], ): Encoder[T] = { (t: T) =&gt; enc.apply(extractor.extract(t)) } And you can also derive decoders, based on HasBuilder instances. Here’s how to automatically derive io.circe.Decoder (validation included): implicit def jsonDecoder[T, S](implicit builder: HasBuilder.Aux[T, S], dec: Decoder[S], ): Decoder[T] = (c: HCursor) =&gt; { dec.apply(c).flatMap { value =&gt; builder.build(value) match { case value @ Right(_) =&gt; value.asInstanceOf[Either[DecodingFailure, T]] case Left(failure) =&gt; val msg = failure.message.fold(\"\")(m =&gt; s\" — $m\") Left(DecodingFailure( s\"Invalid ${failure.typeInfo.typeLabel}$msg\", c.history )) } } } You don’t need to define such encoders and decoders, as they are already defined in the Circe integration. But you can use HasExtractor and HasBuilder for new integrations."
    } ,    
    {
      "title": "Documentation",
      "url": "/docs/",
      "content": "The packages are published on Maven Central: libraryDependencies += \"io.monix\" %% \"newtypes-core\" % \"0.2.1\" Quick sample import monix.newtypes._ // Just for deriving type class instances import cats._ import cats.implicits._ type Firstname = Firstname.Type object Firstname extends NewtypeWrapped[String] { implicit val eq: Eq[Firstname] = derive } // Usage: val name = Firstname(\"Alex\") // Coercing back into String: name.value See the documentation menu for the available topics."
    } ,    
    {
      "title": "Home",
      "url": "/",
      "content": "Monix’s Newtypes Macro-free helpers for defining newtypes in Scala, cross-compiled to Scala 3. Usage The packages are published on Maven Central. libraryDependencies += \"io.monix\" %% \"newtypes-core\" % \"0.2.1\" For the Circe integration: // For Circe version 0.14.x libraryDependencies += \"io.monix\" %% \"newtypes-circe-v0.14\" % \"0.2.1\" NOTE: the version scheme is set to early-semver. Documentation ScalaDoc API Website Motivation Defining Newtypes Circe JSON integration Acknowledgements Encoding was shamelessly copied from the scala-newtype project by Cary Robbins et al. Contributing This project welcomes contributions from anybody wishing to participate. All code or documentation that is provided must be licensed with the same license that Newtypes is licensed with (Apache 2.0, see LICENCE). People are expected to follow the Scala Code of Conduct when discussing Newtypes on GitHub, Gitter channel, or other venues. Feel free to open an issue if you notice a bug, have an idea for a feature, or have a question about the code. Pull requests are also gladly accepted. For more information, check out the contributor guide. License All code in this repository is licensed under the Apache License, Version 2.0. See LICENCE."
    } ,      
    {
      "title": "Motivation",
      "url": "/docs/motivation.html",
      "content": "Motivation for Newtypes In other statically typed languages, such as Haskell, a newtype declaration creates a new type out of an existing one, like a type safe alias. In Scala, this would be a perfectly acceptable newtype declaration, even if not ideal: final case class Surname(value: String) NOTE: the constructor has to take a single parameter. If it takes more than one parameter, technically it isn’t a newtype. The purpose is static type safety: // It's much safe to deal with this: def register( fname: FirstName, lname: LastName, ea: EmailAddress, ): IO[Account] = ??? // ... than to deal with this ... def register( firstName: String, lastName: String, emailAddress: String, ): IO[Account] = ??? Note the invocation: register( \"Alex\", \"Nedelcu\", \"noreply@alexn.org\", ) It’s easy to mix ordering, or to break the signature when we insert a new parameter between the existing ones. With lack of type safety, we have to rely on names: register( firstName = \"Alex\", lastName = \"Nedelcu\", emailAddress = \"noreply@alexn.org\", ) But now we’re down to using discipline, as the compiler can’t protect us. A second usage of newtypes is for working with type classes and defining alternative instances to those already defined, or for defining instances for types that we don’t control. For example, the Ordering type class in Scala has default instances for primitives, but the order is ascending: import scala.math.Ordering import scala.collection.immutable.SortedSet implicitly[Ordering[Int]].compare(1, 2) //=&gt; -1 SortedSet(1, 10, 9, 2, 5, 3) //=&gt; TreeSet(1, 2, 3, 5, 9, 10) If we want a different ordering, it’s a very bad practice to redefine the available instance. The best practice is to define a newtype: case class ReversedInt(value: Int) { override def toString = value.toString } object ReversedInt { implicit val ord: Ordering[ReversedInt] = (x, y) =&gt; -1 * implicitly[Ordering[Int]].compare(x.value, y.value) } SortedSet(List(1, 10, 9, 2, 5, 3).map(ReversedInt(_)):_*) //=&gt; TreeSet(10, 9, 5, 3, 2, 1) The library’s purpose Working with case classes, like the above, is completely fine. However, they have a runtime cost, generating extra boxing and unboxing. Also, you may want to add extra validation, or to derive type class instances. Scala 3 has introduced opaque types for this purpose. These are type-safe aliases that have no added runtime cost. However, it would be nice to have some helpers in a cross-compiled fashion that also provides an encoding compatible with Scala 2. We already had scala-newtype, a pretty awesome project. Monix’s Newtypes is inspired by it. The purpose of this project is to be stable and easy to port. That means no macros, and no magic based on implicits."
    } ,      
  ];

  idx = lunr(function () {
    this.ref("title");
    this.field("content");

    docs.forEach(function (doc) {
      this.add(doc);
    }, this);
  });

  docs.forEach(function (doc) {
    docMap.set(doc.title, doc.url);
  });
}

// The onkeypress handler for search functionality
function searchOnKeyDown(e) {
  const keyCode = e.keyCode;
  const parent = e.target.parentElement;
  const isSearchBar = e.target.id === "search-bar";
  const isSearchResult = parent ? parent.id.startsWith("result-") : false;
  const isSearchBarOrResult = isSearchBar || isSearchResult;

  if (keyCode === 40 && isSearchBarOrResult) {
    // On 'down', try to navigate down the search results
    e.preventDefault();
    e.stopPropagation();
    selectDown(e);
  } else if (keyCode === 38 && isSearchBarOrResult) {
    // On 'up', try to navigate up the search results
    e.preventDefault();
    e.stopPropagation();
    selectUp(e);
  } else if (keyCode === 27 && isSearchBarOrResult) {
    // On 'ESC', close the search dropdown
    e.preventDefault();
    e.stopPropagation();
    closeDropdownSearch(e);
  }
}

// Search is only done on key-up so that the search terms are properly propagated
function searchOnKeyUp(e) {
  // Filter out up, down, esc keys
  const keyCode = e.keyCode;
  const cannotBe = [40, 38, 27];
  const isSearchBar = e.target.id === "search-bar";
  const keyIsNotWrong = !cannotBe.includes(keyCode);
  if (isSearchBar && keyIsNotWrong) {
    // Try to run a search
    runSearch(e);
  }
}

// Move the cursor up the search list
function selectUp(e) {
  if (e.target.parentElement.id.startsWith("result-")) {
    const index = parseInt(e.target.parentElement.id.substring(7));
    if (!isNaN(index) && (index > 0)) {
      const nextIndexStr = "result-" + (index - 1);
      const querySel = "li[id$='" + nextIndexStr + "'";
      const nextResult = document.querySelector(querySel);
      if (nextResult) {
        nextResult.firstChild.focus();
      }
    }
  }
}

// Move the cursor down the search list
function selectDown(e) {
  if (e.target.id === "search-bar") {
    const firstResult = document.querySelector("li[id$='result-0']");
    if (firstResult) {
      firstResult.firstChild.focus();
    }
  } else if (e.target.parentElement.id.startsWith("result-")) {
    const index = parseInt(e.target.parentElement.id.substring(7));
    if (!isNaN(index)) {
      const nextIndexStr = "result-" + (index + 1);
      const querySel = "li[id$='" + nextIndexStr + "'";
      const nextResult = document.querySelector(querySel);
      if (nextResult) {
        nextResult.firstChild.focus();
      }
    }
  }
}

// Search for whatever the user has typed so far
function runSearch(e) {
  if (e.target.value === "") {
    // On empty string, remove all search results
    // Otherwise this may show all results as everything is a "match"
    applySearchResults([]);
  } else {
    const tokens = e.target.value.split(" ");
    const moddedTokens = tokens.map(function (token) {
      // "*" + token + "*"
      return token;
    })
    const searchTerm = moddedTokens.join(" ");
    const searchResults = idx.search(searchTerm);
    const mapResults = searchResults.map(function (result) {
      const resultUrl = docMap.get(result.ref);
      return { name: result.ref, url: resultUrl };
    })

    applySearchResults(mapResults);
  }

}

// After a search, modify the search dropdown to contain the search results
function applySearchResults(results) {
  const dropdown = document.querySelector("div[id$='search-dropdown'] > .dropdown-content.show");
  if (dropdown) {
    //Remove each child
    while (dropdown.firstChild) {
      dropdown.removeChild(dropdown.firstChild);
    }

    //Add each result as an element in the list
    results.forEach(function (result, i) {
      const elem = document.createElement("li");
      elem.setAttribute("class", "dropdown-item");
      elem.setAttribute("id", "result-" + i);

      const elemLink = document.createElement("a");
      elemLink.setAttribute("title", result.name);
      elemLink.setAttribute("href", result.url);
      elemLink.setAttribute("class", "dropdown-item-link");

      const elemLinkText = document.createElement("span");
      elemLinkText.setAttribute("class", "dropdown-item-link-text");
      elemLinkText.innerHTML = result.name;

      elemLink.appendChild(elemLinkText);
      elem.appendChild(elemLink);
      dropdown.appendChild(elem);
    });
  }
}

// Close the dropdown if the user clicks (only) outside of it
function closeDropdownSearch(e) {
  // Check if where we're clicking is the search dropdown
  if (e.target.id !== "search-bar") {
    const dropdown = document.querySelector("div[id$='search-dropdown'] > .dropdown-content.show");
    if (dropdown) {
      dropdown.classList.remove("show");
      document.documentElement.removeEventListener("click", closeDropdownSearch);
    }
  }
}
