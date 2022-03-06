# Пример OSGi-bundle для NCALayer с использованием Common Invoker

[(Пример без Common Invoker)](https://github.com/pkigovkz/NCALayerSampleBundle)

Модуль NCALayer (OSGi bundle) - подписанный jar, в манифесте которого имеются свойства:
- *Bundle-SymbolicName* - символическое имя (уникальный идентификатор)
- *Bundle-Name* - наименование
- *Bundle-Version* - версия
- *Export-Package* - список пакетов, к которым можно дать доступ для других модулей. Указывать нужно полное название пакета, без wildcard.
- *Import-Package* - список пакетов, к которым нужен доступ. Основные экспортируемые пакеты определяются в **syspkgs** ядра [NCALayer](https://github.com/pkigovkz/NCALayer). Другие пакеты экспортируются модулями NCALayer. Указывать нужно полное название пакета, без wildcard. Неправильно - _javax.xml.*_, правильно - *javax.xml.parsers*
- *Bundle-Activator* - полное наименование класса, реализующего интерфейс *org.osgi.framework.BundleActivator*. На данный момент активатор нужен для регистрации сервисов при запуске модуля.

Сервис (класс, реализующий *kz.gov.pki.osgi.layer.api.ModuleService*) объявляет и регистрирует классы с аннотацией *@NCALayerClass*. В одном модуле можно объявить более одного класса *@NCALayerClass*. Класс перечисляет доступные для NCALayer методы аннотацией *@NCALayerMethod*. При регистрации сервиса в активаторе необходимо задать уникальное значение *module*, по которому можно к нему обратиться. *module* не зависит от *Bundle-SymbolicName* и может отличаться, так как в одном модуле может быть реализовано более одного сервиса.

<u>Рекомендуется</u> использовать осмысленные значения. Например:

- *Bundle-SymbolicName: <u>kz.yourcompany.yourbundle</u>*
- *module: <u>kz.yourcompany.yourbundle.yourservice</u>*

Данные передаются в формате JSON. Например, запрос к модулю из примера выглядит следующим образом:
```
{
  "apiVersion": 2, -- опционален, по умолчанию 2
  "module": "kz.gov.pki.SampleBundle",
  "class": "SampleNLClass", -- опционален, требуется если регистрируется более одного класса
  "method": "salemAit",
  "args": {
    "firstname": "Naruto",
  	"lastname": "Uzumaki"       
	}
}
```
На что можно получить следующий ответ:
```
{
  "body": "Salem, Naruto Uzumaki",
 	"status": true
}
```

Пример отправки некорректных параметров для метода (отсутствует firstname):

```
{
  "module": "kz.gov.pki.SampleBundle",
  "method": "salemAit",
  "args": {
     "lastname": "Uzumaki"       
   }
}
```

Ответ с ошибкой:

```
{
  "errorCode": "firstname_is_empty",
  "message": "Name yourself!",
  "status":false
}
```



## Общедоступные сервисы

- *LogService* - сервис логирования для записи событий в общий журнал (ncalayer.log).
- *NCALayerService* - сервис может предоставить экземпляр зарегистрированного KalkanProvider.
- Запрос для получения списка установленных модулей с версиями:
  ```
  {
      module: "kz.gov.pki.ncalayerservices.accessory",
      method: "getBundles"
  }
  ```
  Ответ:
  ```
  {
      "kz.gov.pki.api.layer.NCALayerServices": "0.5.0",
      "kz.gov.pki.osgi.layer.websocket": "0.3.3",
      "kz.gov.pki.kalkan.knca_provider_jce_kalkan": "0.5.0"
  }
  ```
- Запрос для получения списка зарегистрированных сервисов:
  ```
  {
      module: "kz.gov.pki.ncalayerservices.accessory",
      method: "getServices”
  }
  ```
  Ответ:
  ```
  {
      "services": [
          "kz.gov.pki.cms.CMSSignUtil",
          "kz.gov.pki.ncalayerservices.accessory"
      ]
  }
  ```

## Тестирование нового модуля

Для начала нужно запросить у НУЦ РК *ncalayer.der* с данными модуля для локального тестирования. Для этого необходимо предоставить следующие данные о модуле:
- *Bundle-SymbolicName*
- *сертификат подписи кода*
- *наименование*

После получения *ncalayer.der* нужно заменить оригинальный файл, расположенный в домашней директории NCALayer. Это позволит устанавливать и обновлять свой модуль локально без ограничений, путем копирования jar в папку *bundles*. При этом NCALayer не сможет запрашивать обновления с интернета. При локальных обновлениях нужно либо повышать *Bundle-Version* нового jar, либо удалять из папки *ncalayer-cache* папку старого jar (папка bundleX - где X число, обычно модуль установленный последним имеет наибольшее значение).

## Дополнительные возможности

В NCALayer существует возможность установки модуля по запросу при его отсутствии в числе установленных модулей. Данной функцией можно воспользоваться следующим образом:
1. После подключения к NCALayer есть два варианта проверки того, что модуль не установлен. Веб-ресурс:
   - запрашивает список установленных модулей и не находит в полученном списке необходимого модуля
   - вызывает какую-либо функцию модуля и в ответ получает:
     ```
     {
         "success": false,
         "errorCode": "MODULE_NOT_FOUND"
     }
     ```
2. Если модуль не найден, то отправляется запрос на установку необходимого модуля по *Bundle-SymbolicName*:
   ```
   {
       module: "kz.gov.pki.ncalayerservices.accessory", 
       method: "installBundle",
       symname: "kz.yourcompany.yourbundle"
   }
   ```
3. Пользователь получает сообщение о том, что необходимо установить дополнительный модуль. В случае успешной загрузки модуля, NCALayer попросит разрешения на перезапуск.
4. После перезапуска NCALayer модуль будет готов к использованию.
