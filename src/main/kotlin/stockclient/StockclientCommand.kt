package stockclient

import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import io.micronaut.retry.annotation.CircuitBreaker

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import javax.inject.Inject

@Client(id="stockprices")
//Quando houver uma falha, espere um segundo para o servidor ser recuperar
//tente 5 vezes e se falhar todas as cinco vezes você pode desistir
//observe que a cada tentativa o server discovery irá encaminhar a requisição
// para uma instância diferente do ms
@CircuitBreaker(delay ="1s", attempts = "5" )
interface StockPriceClient {
    @Get("/price/{tickers}")
    fun getPrices(tickers: String): List<Stock>
}

@Command(
    name = "stockclient", description = ["..."],
    mixinStandardHelpOptions = true
)
class StockclientCommand : Runnable {
    @Inject
    lateinit var stockPriceClient: StockPriceClient

    override fun run() {
        var tickers = "GOOG, AMZN, ORCL"

        println("Getting stock prices for $tickers")

        stockPriceClient.getPrices(tickers)
            .forEach(::println)

//        stockPriceClient.getPrices("AMZN")
//            .forEach(::println)
    }                                                                                                                                                               

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PicocliRunner.run(StockclientCommand::class.java, *args)
        }
    }
}
