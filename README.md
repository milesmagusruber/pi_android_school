# PI-school 2019

# Week 10
 - Уведомления 
 
   - https://developer.android.com/training/best-background
   - https://developer.android.com/topic/libraries/architecture/workmanager
   - https://developer.android.com/guide/topics/ui/notifiers/notifications
   - https://developer.android.com/reference/android/app/Service
 - Задания и codelabs
    - https://codelabs.developers.google.com/codelabs/notification-channels-java/index.html?index=..%2F..index#0
    - https://codelabs.developers.google.com/codelabs/android-workmanager/index.html?index=..%2F..index#0
    - Работаем на основане приложения с прошлой недели:
      - Добавляем экран настроек (у кого ещё нет)
      - На этом экране добавляем возможность включать/отключать запросы приложения на обновление фоточек по какому то запросу в фоне (смотрите файл Week10.png)
      - Если пользователь разрешает такие обновления - разрешаем выбрать срок периодичности запросов
      - Интервалы - 15 минут, 30 минут, 1 час, 6 часов, сутки
      - Приложение, работая в фоне, должно будет с указанной периодичностью ходить на сервер и получать новые фотографии
      - После успешного обновления сохранять их в отдельную таблицу в базе, добавить возможность просмотра этих фотографий на отдельном экране (фрагменте)
      - После успешного обновления показывать уведомление в Notification Panel
      - В уведомлении отображать количество полученных фотографий
      - При нажатии на уведомление переходить на экран просмотра фотографий
      - При желании можно сделать чтобы в уведомлении показывалась первая картинка из загруженных