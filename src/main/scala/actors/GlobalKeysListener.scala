package actors

import akka.actor.typed.{ActorRef, Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.{NativeKeyAdapter, NativeKeyEvent}

import java.util.logging.{Level, Logger}
import scala.collection.mutable

object GlobalKeysListener:
  private val logger = Logger.getLogger(classOf[GlobalScreen].getPackage.getName)
  logger.setLevel(Level.WARNING)
  logger.setUseParentHandlers(false)

  private val hotKeys = List(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_SEMICOLON)
  private val pressedHotKeys = mutable.Map(hotKeys.map(_ -> false).toMap.toSeq *)

  private def isInHotKeys(code: Int): Boolean = hotKeys.contains(code)
  private def setHotKeyPressed(code: Int, value: Boolean): Unit = pressedHotKeys(code) = value
  private def isAllKeyPressed: Boolean = pressedHotKeys.values.forall(identity)

  def apply[T](hotKeysReceiver: ActorRef[T], message: T): Behavior[Nothing] = Behaviors.setup: ctx =>
    import ctx.*

    GlobalScreen.registerNativeHook()

    val listener =
      new NativeKeyAdapter:
        override def nativeKeyPressed(event: NativeKeyEvent): Unit =
          val code = event.getKeyCode
          if !isInHotKeys(code) then
            return

          setHotKeyPressed(code, true)
          if isAllKeyPressed then
            hotKeysReceiver ! message

        override def nativeKeyReleased(event: NativeKeyEvent): Unit = event.getKeyCode match
          case code if isInHotKeys(code) => setHotKeyPressed(code, false)
          case _ =>

    GlobalScreen.addNativeKeyListener(listener)

    Behaviors
      .receiveSignal:
        case (_, PostStop) =>
          log.info("Global input listening stopped")
          GlobalScreen.removeNativeKeyListener(listener)

          if GlobalScreen.isNativeHookRegistered then GlobalScreen.unregisterNativeHook()
          Behaviors.same
