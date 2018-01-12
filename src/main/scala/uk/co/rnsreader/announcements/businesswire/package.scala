package uk.co.rnsreader.announcements

import shapeless.tag.@@

package object businesswire {
  trait BusinessWireBaseUrlTag
  type BusinessWireBaseUrl = String @@ BusinessWireBaseUrlTag
}

