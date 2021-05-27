package cafe.adriel.lyricist.processor.internal

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

internal class LyricistVisitor(
    private val declarations: MutableList<KSPropertyDeclaration>
) : KSVisitorVoid() {

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        declarations.add(property)
    }
}
