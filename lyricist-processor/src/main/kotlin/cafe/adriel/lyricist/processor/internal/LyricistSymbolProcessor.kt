package cafe.adriel.lyricist.processor.internal

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

internal class LyricistSymbolProcessor(
    private val processors: List<SymbolProcessor>,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        processors.forEach { processor ->
            processor.process(resolver)
        }
        return emptyList()
    }
}
