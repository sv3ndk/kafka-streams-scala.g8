package $package$

import java.util.{Calendar, Properties}

import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import com.lightbend.kafka.scala.streams.ImplicitConversions._
import com.lightbend.kafka.scala.streams.{KStreamS, StreamsBuilderS}
import com.lightbend.kafka.scala.streams.DefaultSerdes._
import org.apache.kafka.streams.kstream.Printed

/**
  * Make sure to create 2 topics before running this:
  *
  *    kafka-topics --create --zookeeper localhost:2181 --partitions 1 --replication-factor 1 --topic rawSentences
  *
  *    kafka-topics --create --zookeeper localhost:2181 --partitions 1 --replication-factor 1 --topic  countedTokens
  *
  * Then add some content in the first one:
  *
  *   kafka-console-producer --broker-list localhost:9092  --topic rawSentences
  *
  * which you can check by just tailing that topic:
  *
  *   kafka-console-consumer --bootstrap-server localhost:9092 --topic rawSentences --from-beginning
  *
  * One the programme below runs (e.g. through "sbt run"), you can simply inspect its output with:
  *
  *   kafka-console-consumer --bootstrap-server localhost:9092 --key-deserializer org.apache.kafka.common.serialization.StringDeserializer --value-deserializer org.apache.kafka.common.serialization.LongDeserializer --from-beginning --topic countedTokens
  *
  *
  * */

object WordCount extends App {

  val config: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application" + Calendar.getInstance.getTimeInMillis)
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")

    // helps for debug (not optimal for prod): makes KTable data immediately available, without de-duplications
    p.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0)
    p
  }

  val builder = new StreamsBuilderS()
  val textLines = builder.stream[String, String]("rawSentences")

  val counted: KStreamS[String, Long] = textLines
    .flatMapValues( line => line.split("\\W+").toIterable )
    .map( (_, w) => (w, 1l))
    .groupByKey
    .count
    .toStream

  // print to console
  counted.print(Printed.toSysOut[String, Long])


  // serialize to topic, with default Serdes
  counted.to("countedTokens")

  val streams: KafkaStreams = new KafkaStreams(builder.build, config)
  streams.start()

}


