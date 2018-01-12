package uk.co.rnsreader.announcements

import shapeless.tag.@@


package object rns {
  trait TrustNetBaseUrlTag
  type TrustNetBaseUrl = String @@ TrustNetBaseUrlTag
}
