package actors

import akka.actor.typed.{Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors
import org.jnativehook.keyboard.{NativeKeyAdapter, NativeKeyEvent}
import org.jnativehook.{GlobalScreen, NativeHookException}
import scala.collection.mutable

object Application:
  enum Command:
    case HotKeysPressed

  def apply(): Behavior[Command] = Behaviors.setup: ctx =>
    import ctx.*
    import Command.*

    try GlobalScreen.registerNativeHook()
    catch
      case ex: NativeHookException =>
        log.error("Неудалось зарегистрировать прослушивание клавиатуры", ex)
        system.terminate()

    val hotKeys = List(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_SEMICOLON)
    val pressedHotKeys = mutable.Map(hotKeys.map(key => key -> false).toMap.toSeq *)

    GlobalScreen.addNativeKeyListener:
      new NativeKeyAdapter:
        override def nativeKeyPressed(event: NativeKeyEvent): Unit = event.getKeyCode match
          case code if isInHotKeys(code) =>
            setHotKeyPressed(code, value = false)
            if (isAllKeyPressed) ctx.self ! HotKeysPressed

          case _ =>

        override def nativeKeyReleased(event: NativeKeyEvent): Unit = event.getKeyCode match
          case code if isInHotKeys(code) => setHotKeyPressed(code, value = false)
          case _ =>

        private def isInHotKeys(code: Int): Boolean = hotKeys.contains(code)
        private def setHotKeyPressed(code: Int, value: Boolean): Unit = pressedHotKeys(code) = value
        private def isAllKeyPressed: Boolean = pressedHotKeys.values.forall(identity)

    val window = spawnAnonymous(Window())

    Behaviors
      .receiveMessage[Command]:
        case HotKeysPressed =>
          window ! Window.Command.ToggleActivate
          Behaviors.same

      .receiveSignal:
        case (_, PostStop) =>
          if GlobalScreen.isNativeHookRegistered then
            try GlobalScreen.unregisterNativeHook()
            catch
              case ex =>
                log.error("Ошибка отключения прослушивания клавиатуры", ex)
          Behaviors.same
